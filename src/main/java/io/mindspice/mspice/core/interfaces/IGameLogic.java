package io.mindspice.mspice.core.logic;

public interface ILogic {

    void init() throws Exception;

    void input();

    void render();

    void cleanup();

}
