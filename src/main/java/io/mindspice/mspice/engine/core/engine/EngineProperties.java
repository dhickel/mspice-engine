package io.mindspice.mspice.engine.core.engine;

import java.io.*;
import java.util.Properties;


public class EngineProperties {
    private static final String FILENAME = "eng.properties";
    private static EngineProperties instance = new EngineProperties();

    // Settings
    private int ups;
    private boolean validate;
    private String phDeviceName;
    private boolean vSync;
    private int requestedImages;




    private EngineProperties() {
        // Singleton
        Properties props = new Properties();

        try (InputStream stream = EngineProperties.class.getResourceAsStream("/" + FILENAME)) {
            props.load(stream);
            ups = Integer.parseInt(props.getOrDefault("ups", 60).toString());
            validate = Boolean.parseBoolean(props.getOrDefault("vkValidate", false).toString());
            phDeviceName = props.getOrDefault("phDeviceName", "None" ).toString();
            vSync = Boolean.parseBoolean(props.getOrDefault("vsync", true).toString());
            requestedImages = Integer.parseInt(props.getOrDefault("requestedImages", 3).toString());

        } catch (IOException excp) {
            System.out.println("Could not read path: " + FILENAME + " | " + excp);
        }
    }

    public static synchronized EngineProperties getInstance() {
        return instance;
    }

    public int ups() {
        return ups;
    }


    public String phDeviceName() {
        return phDeviceName;
    }
    public boolean vkValidate() {
        return validate;
    }

    public int requestedImages() {
        return requestedImages;
    }

    public boolean vSync() {
        return vSync;
    }
}