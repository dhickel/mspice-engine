package io.mindspice.mspice.engine.core.renderer;

import io.mindspice.mspice.engine.core.engine.EngineProperties;
import io.mindspice.mspice.engine.core.window.Window;


public class Render {
    private final Instance instance;
    private final Device device;
    private final Queue.GraphicsQueue graphQueue;
    private final PhysicalDevice physicalDevice;
    private final Surface surface;
    private SwapChain swapChain;

    private final CommandPool commandPool;
    private final Queue.PresentQueue presentQueue;
    private final ForwardRenderActivity fwdRenderActivity;

    public Render(Window window, Scene scene) {
        EngineProperties engProps = EngineProperties.getInstance();
        instance = new Instance(engProps.vkValidate());
        physicalDevice = PhysicalDevice.createPhysicalDevice(instance, engProps.phDeviceName());
        device = new Device(physicalDevice);
        surface = new Surface(physicalDevice, window.getWindowHandle());
        graphQueue = new Queue.GraphicsQueue(device, 0);
        presentQueue = new Queue.PresentQueue(device, surface, 0);
        swapChain = new SwapChain(device, surface, window, engProps.requestedImages(), engProps.vSync(),
                presentQueue, new Queue[]{graphQueue});
        commandPool = new CommandPool(device, graphQueue.getQueueFamilyIndex());
        fwdRenderActivity = new ForwardRenderActivity(swapChain, commandPool);
    }

    public void cleanup() {
        presentQueue.waitIdle();
        graphQueue.waitIdle();
        device.waitIdle();
        fwdRenderActivity.cleanup();
        commandPool.cleanup();
        swapChain.cleanup();
        surface.cleanup();
        device.cleanup();
        physicalDevice.cleanup();
        instance.cleanup();
    }


    public void render(Window window, Scene scene) {
        System.out.println("Rendering");
        fwdRenderActivity.waitForFence();

        swapChain.acquireNextImage();

        fwdRenderActivity.submit(graphQueue);

        swapChain.presentImage(presentQueue);
    }
}
