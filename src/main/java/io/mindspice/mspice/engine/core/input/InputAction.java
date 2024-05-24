package io.mindspice.mspice.engine.enums;

public enum InputAction {
    RESIZE_SCREEN(ActionType.SCREEN),
    MOVE_UP(ActionType.GAME_INPUT),
    MOVE_DOWN(ActionType.GAME_INPUT),
    MOVE_LEFT(ActionType.GAME_INPUT),
    MOVE_RIGHT(ActionType.GAME_INPUT),
    SCROLL_UP(ActionType.GAME_INPUT),
    SCROLL_DOWN(ActionType.GAME_INPUT);

    public final ActionType actionType;

    InputAction(ActionType actionType) { this.actionType = actionType; }
}
