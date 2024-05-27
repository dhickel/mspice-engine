package io.mindspice.mspice.engine.core.renderer.opengl;

import imgui.*;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiKey;
import imgui.type.ImInt;
import io.mindspice.mspice.engine.core.engine.CleanUp;
import io.mindspice.mspice.engine.core.engine.InputListener;
import io.mindspice.mspice.engine.core.graphics.primatives.GuiMesh;
import io.mindspice.mspice.engine.core.graphics.primatives.Texture;
import io.mindspice.mspice.engine.core.input.*;
import io.mindspice.mspice.engine.core.renderer.components.GuiScene;
import io.mindspice.mspice.engine.core.renderer.components.UniformsMap;
import io.mindspice.mspice.engine.core.window.Window;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL32.*;


public class GuiRenderer implements InputListener, CleanUp {
    private ImGuiIO imGui;

    private GuiMesh guiMesh;
    private GLFWKeyCallback prevKeyCallBack;
    private Vector2f scale;
    private ShaderProgram shaderProgram;
    private Texture texture;
    private UniformsMap uniformsMap;
    private KeyCallBackListener keyListener;
    private boolean enabled = false;
    private GuiScene guiScene;

    public GuiRenderer(Window window) {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/home/mindspice/code/Java/game/mspice-engine/src/main/resources/gui.vsh", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("/home/mindspice/code/Java/game/mspice-engine/src/main/resources/gui.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();
        createUIResources(window);

    }

    public ImGuiIO getImGui() {
        return imGui;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void loadScene(GuiScene guiScene) {
        this.guiScene = guiScene;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void cleanup() {
        shaderProgram.cleanup();
        texture.cleanup();
        if (prevKeyCallBack != null) {
            prevKeyCallBack.free();
        }
    }

    private void createUIResources(Window window) {
        ImGui.createContext();

        imGui = ImGui.getIO();
        imGui.setIniFilename(null);
        imGui.setDisplaySize(window.getWidth(), window.getHeight());

        ImFontAtlas fontAtlas = ImGui.getIO().getFonts();
        ImInt width = new ImInt();
        ImInt height = new ImInt();
        ByteBuffer buf = fontAtlas.getTexDataAsRGBA32(width, height);
        texture = new Texture(width.get(), height.get(), buf);

        guiMesh = new GuiMesh();
    }

    private void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("scale");
        scale = new Vector2f();
    }

    @Override
    public void registerListener(InputManager inputManager) {
        // imGui.setKeyMap(ImGuiKey.Tab, InputAction.GUI_TAB.ordinal());
        imGui.setKeyMap(ImGuiKey.LeftArrow, InputAction.GUI_LEFT.ordinal());
        imGui.setKeyMap(ImGuiKey.RightArrow, InputAction.GUI_RIGHT.ordinal());
        imGui.setKeyMap(ImGuiKey.UpArrow, InputAction.GUI_UP.ordinal());
        imGui.setKeyMap(ImGuiKey.DownArrow, InputAction.GUI_DOWN.ordinal());
        imGui.setKeyMap(ImGuiKey.Enter, InputAction.GUI_ENTER.ordinal());

        keyListener = new KeyCallBackListener(
                ActionType.GUI,
                Arrays.stream(InputAction.values()).filter(e -> e.actionType == ActionType.GUI).toList(),
                10,
                this::keyInputConsumer
        );

        inputManager.regKeyListener(keyListener);
    }

    @Override
    public void setListening(boolean listening) {
        keyListener.setListening(listening);
    }

    @Override
    public boolean isListening() {
        return keyListener.isListening();
    }

    private void keyInputConsumer(InputAction action, int value) {
        if (value == 2) { return; }
        imGui.setKeysDown(action.ordinal(), value == 1);
    }

    public void render() {
        if (!enabled || guiScene == null) { return; }
        guiScene.render();

        shaderProgram.bind();

        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glBindVertexArray(guiMesh.getVaoId());

        glBindBuffer(GL_ARRAY_BUFFER, guiMesh.getVerticesVBO());
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, guiMesh.getIndicesVBO());

        ImGuiIO io = ImGui.getIO();
        scale.x = 2.0f / io.getDisplaySizeX();
        scale.y = -2.0f / io.getDisplaySizeY();
        uniformsMap.setUniform("scale", scale);

        ImDrawData drawData = ImGui.getDrawData();
        int numLists = drawData.getCmdListsCount();
        for (int i = 0; i < numLists; i++) {
            glBufferData(GL_ARRAY_BUFFER, drawData.getCmdListVtxBufferData(i), GL_STREAM_DRAW);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, drawData.getCmdListIdxBufferData(i), GL_STREAM_DRAW);

            int numCmds = drawData.getCmdListCmdBufferSize(i);
            for (int j = 0; j < numCmds; j++) {
                final int elemCount = drawData.getCmdListCmdBufferElemCount(i, j);
                final int idxBufferOffset = drawData.getCmdListCmdBufferIdxOffset(i, j);
                final int indices = idxBufferOffset * ImDrawData.SIZEOF_IM_DRAW_IDX;

                texture.bind();
                glDrawElements(GL_TRIANGLES, elemCount, GL_UNSIGNED_SHORT, indices);
            }
        }

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        glDisable(GL_BLEND);
    }

    public void resize(int width, int height) {
        ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setDisplaySize(width, height);
    }

}