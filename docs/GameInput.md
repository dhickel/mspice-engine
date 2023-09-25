
# GameInput


## Game Input Class Overview

> Handles input from mouse, keyboard and later game pads. Class is not implemented as a singleton but should be treated as such for most uses. Only one instance should exist for a given set of inputs. In a single player game there should only be one, but by not implementing as a singleton co-op input is a possibility with an instance created for each player.  Listeners are registered via the ```reg<Type>Listener(KeyListener)``` methods.

>This class is a centralized place to keep all key listeners and to register and unregister them as needed. It contains callbacks implementations for GLFW input callbacks. When a call back is received the input event is broadcast to all of the registered listeners.

> This class's sole purpose is to handle the callbacks marshalling and  broadcasting to the listeners which are a form of sorting the input. Listeners can overlap in the inputs they listen for, while having different actions done on consumption. This all for there to be a listener say for menu actions, game actions, and different view of any sort that can be register and un-registered on an as-needed basis. So say when a menu is open the listener for it can be registered, and others un-registered, Or they can stay registered with the shared bindings but other listeners can have their queue not processed and cleared if it is out of focus.

**Game Input Snippet**
```java

private final KeyEventManager keyboardEvents = new KeyEventManager(10);
private final KeyEventManager mouseEvents = new KeyEventManager(10);
private final KeyEventManager scrollEvents = new KeyEventManager(10);


  public GameInput() {
        keyInputCallBack = new GLFWKeyCallback() {
            @Override // Release = 0, Press = 1, Repeat =2
            public void invoke(long window, int key, int scanCode, int action, int mods) {
                keyboardEvents.broadcast(key, action); // Broadcast to all registered listeners
            }
        };
        mouseInputCallBack = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                mouseEvents.broadcast(button, action);
            }
        };
        mousePosCallBack = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
                mousePosEvents.broadcast(x, y);
            }
        };
        mouseScrollCallBack = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double offsetX, double offsetY) {
                if (offsetX > 0) {
                    scrollEvents.broadcast(0,1);
                } else if(offsetX < 0) {
                    scrollEvents.broadcast(0,0);
                }

            }
        };
    }

// Register Listeners

public void regKeyboardListener(KeyListener listener) {
    keyboardEvents.registerListener(listener);
}

public void regMouseButtonListener(KeyListener listener) {
    mouseEvents.registerListener(listener);
}

public void regScrollListener(KeyListener listener) {
    scrollEvents.registerListener(listener);
}
```


### KeyEvenManager
> KeyEventManagers are the competent that holds the registered listeners for various input types. When in input is received from a GLFW callback broadcast as outlined in the code above, it is relayed to the KeyEventManager for the related input type. When a broadcast is called from the main GameInput class the KeyEventManager loops through call of the KeyListeners that have been registered and offers them the new input as shown in the snippet below:

```java
 // @Override
    public void broadcast(int keyCode, int keyAction) {
        InputAction inputAction = inputMap.get(keyCode);
        if (inputAction == null) { return; }
        for (int i = 0; i < size; i++) {
            var listener = keyListeners[i];
            if (listener.isListening() && listener.isListenerFor(inputAction.actionType)) {
                listener.offerInput(inputAction, keyAction);
            }
        }
    }
```

> The sole purpose of the KeyManager is to  encapsulate a group of registered listeners for a given input type, facilitate the registering and un-registering of listeners, and relaying broadcasts to the appropriate listeners for a given actionType.


#### ***Input Action***
> As also seen in the line:  ```InputAction inputAction = inputMap.get(keyCode);```  Before an input is offered to a Listener it is looked up and converted to the InputAction. InputAction is an enum class of predefined Actions with an associated ActionType.

#### ***ActionType***
> Actiontype is an enum class that is used to group input actions by their action type. As mentioned prior then can be used to classify different input actions by the type of action they are for ex. game input, menu input, keys that are always active (function keys to close, full screen etc.)


### KeyListener

> Key listeners are registered with KeyEventManagers in the gameInput class, they contain queues that are offered inputs b the broadcast method of KeyEventManager. They are instanced with the following constructor:

```java
    public KeyListener(ActionType[] listeningFor, int queueSize) {
        this.listeningFor = listeningFor;
        inputQueue = new CircularKeyQueue(queueSize);
    }
```

> They Contain the following methods:

```java
  public boolean isListenerFor(ActionType actionType) {
        for (int i = 0; i < listeningFor.length; i++) {
            if (listeningFor[i] == actionType) {
                return true;
            }
        }
        return false;
    }

    // Adds input to the queue if it is actively listening
    public void offerInput(InputAction inputAction, int keyAction) {
        if (!isListening) { return; }
        inputQueue.add(inputAction, keyAction);
    }


    // Toggle listening
    public void setListening(boolean isListening) {
        this.isListening = isListening;
    }

    // Get listening state
    public boolean isListening() {
        return isListening;
    }

    // Consume input
    public void consume(KeyActionConsumer consumer) {
        inputQueue.poll(consumer);
    }

    // Get the raw queue, can be a foot gun if treated incorrectly, aka do not mutate other than consume
    public CircularKeyQueue getQueue() {
        return inputQueue;
    }

    // Get what actions are being listen to
    public ActionType[] getListeningFor() {
        return listeningFor;
    }

```

> A KeyListener can listen to multiple ActionTypes, though most of the time best practice would be to listen to only one. The KeyListener holds a circular queue of inputs that is defined in the constructor. They are meant in most cases to be queried each frame. A design decision was take to have the roll over, since they are meant to be queried each frame this buffer(queue) should be set to a fairly small size with the assumption it will not overrun, but in the case of an overrun it will favor the most recent inputs. A small queue size is recommended to be used for the constructor but this is up to developer discretion. It should be sized enough to accept as many inputs that may happen between consumptions. Also offers the option to reject if it is full.


<br>

### ***CircularKeyQueue***

> Most of what circular key queue does is outlined above, it is basically a buffer that wraps around and overwrites when it reaches it max size, unless flagged not to.

```java
public class CircularKeyQueue {
    private final InputAction[] inputActions;
    private final int[] keyStates;
    private final int capacity;
    private int head;
    private int tail;
    private volatile int size = 0;
    private  boolean rejectIfFull;

    public CircularKeyQueue(int capacity) {
        this.capacity = capacity;
        this.inputActions = new InputAction[capacity];
        this.keyStates = new int[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;

    }

    public boolean isEmpty() {
        return size == 0;

    }

    public boolean isFull() {
        return size == capacity;
    }

    public void setRejectIfFull(boolean doReject) {
        rejectIfFull = doReject;
    }

    // Adds a new element to the queue if there is space available. Modulo operation is used to wrap around the tail
    // index when it reaches the end of the capacity. This allows it to wrap around.


    public void add(InputAction inputAction, int keyState) {
        if (rejectIfFull && isFull()) { return; }
        inputActions[tail] = inputAction;
        keyStates[tail] = keyState;
        tail = (tail + 1) % capacity;
        if (size < capacity) {
            size++;
        }
        // Removed debug print
    }

    public void poll(KeyActionConsumer consumer) {
        if (!isEmpty()) {
            consumer.accept(inputActions[head], keyStates[head]); // Consume before moving head
            head = (head + 1) % capacity;
            size--;
        }
    }

    public void consumeAll(IntConsumer[] consumers) {
        while (!isEmpty()) {
            if (consumers[inputActions[head].ordinal()] != null) {
                consumers[inputActions[head].ordinal()].accept(keyStates[head]);
            }
            head = (head + 1) % capacity;
            size--;
        }
    }


    public void peek(KeyActionConsumer consumer) {
        if (!isEmpty()) {
            consumer.accept(inputActions[head], keyStates[head]);
        }
    }
}
```

<br>

### ***KeyActionConsumer***
> KeyConsumer is the consumer that is passed to the consumeAll/Pool methods of the queue, These are used to map method callbacks for specific InputActions to methods that handle the logic to perform on conceptions. This  allows for the GameInput to remain encapsulated and not heavily tied to the game state. When the queue is to be consumed anywhere in various GameState a consumer can be passed to handle  specific inputActions. The State that consumes the queue should ideally keep an array of all its consumers for various actions and pass them to consumeAll every frame.

```java
@FunctionalInterface
public interface KeyActionConsumer {
    void accept(InputAction a, int b);
}
```
