package io.mindspice.mspice.engine.core.input;

public enum InputAction {
    RESIZE_WINDOW(ActionType.WINDOW),
    CLOSE_WINDOW(ActionType.WINDOW),
    MOVE_UP(ActionType.GAME_INPUT),
    MOVE_DOWN(ActionType.GAME_INPUT),
    MOVE_LEFT(ActionType.GAME_INPUT),
    MOVE_RIGHT(ActionType.GAME_INPUT),
    SCROLL_UP(ActionType.GAME_INPUT),
    SCROLL_DOWN(ActionType.GAME_INPUT);

    public final ActionType actionType;

    InputAction(ActionType actionType) { this.actionType = actionType; }
}
