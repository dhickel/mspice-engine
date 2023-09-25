package io.mindspice.mspice.enums;

public enum InputAction {
    RESIZE_SCREEN(ActionType.SCREEN),
    MOVE_UP(ActionType.GAME_INPUT);

    public final ActionType actionType;

    InputAction(ActionType actionType) { this.actionType = actionType; }
}
