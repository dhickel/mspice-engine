package io.mindspice.mspice.engine.core.renderer;

import io.mindspice.mspice.engine.core.engine.CleanUp;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static io.mindspice.mspice.engine.core.renderer.VulkanUtils.vkCheck;
import static org.lwjgl.vulkan.VK10.VK_QUEUE_GRAPHICS_BIT;
import static org.lwjgl.vulkan.VK10.vkEnumeratePhysicalDevices;


public class PhysicalDevice implements CleanUp {

    private final VkExtensionProperties.Buffer vkDeviceExtensions;
    private final VkPhysicalDeviceMemoryProperties vkMemoryProperties;
    private final VkPhysicalDevice vkPhysicalDevice;
    private final VkPhysicalDeviceFeatures vkPhysicalDeviceFeatures;
    private final VkPhysicalDeviceProperties vkPhysicalDeviceProperties;
    private final VkQueueFamilyProperties.Buffer vkQueueFamilyProps;

    private PhysicalDevice(VkPhysicalDevice vkPhysicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.vkPhysicalDevice = vkPhysicalDevice;

            IntBuffer intBuffer = stack.mallocInt(1);

            // Get device properties
            vkPhysicalDeviceProperties = VkPhysicalDeviceProperties.calloc();
            VK11.vkGetPhysicalDeviceProperties(vkPhysicalDevice, vkPhysicalDeviceProperties);

            // Get device extensions
            vkCheck(VK11.vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, (String) null, intBuffer, null),
                    "Failed to get number of device extension properties");
            vkDeviceExtensions = VkExtensionProperties.calloc(intBuffer.get(0));
            vkCheck(VK11.vkEnumerateDeviceExtensionProperties(vkPhysicalDevice, (String) null, intBuffer, vkDeviceExtensions),
                    "Failed to get extension properties");

            // Get Queue family properties
            VK11.vkGetPhysicalDeviceQueueFamilyProperties(vkPhysicalDevice, intBuffer, null);
            vkQueueFamilyProps = VkQueueFamilyProperties.calloc(intBuffer.get(0));
            VK11.vkGetPhysicalDeviceQueueFamilyProperties(vkPhysicalDevice, intBuffer, vkQueueFamilyProps);

            vkPhysicalDeviceFeatures = VkPhysicalDeviceFeatures.calloc();
            VK11.vkGetPhysicalDeviceFeatures(vkPhysicalDevice, vkPhysicalDeviceFeatures);

            // Get Memory information and properties
            vkMemoryProperties = VkPhysicalDeviceMemoryProperties.calloc();
            VK11.vkGetPhysicalDeviceMemoryProperties(vkPhysicalDevice, vkMemoryProperties);
        }
    }


    public static PhysicalDevice createPhysicalDevice(Instance instance, String preferredDeviceName) {
        System.out.println("Selecting physical devices");
        PhysicalDevice selectedPhysicalDevice = null;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Get available devices
            PointerBuffer pPhysicalDevices = getPhysicalDevices(instance, stack);
            int numDevices = pPhysicalDevices != null ? pPhysicalDevices.capacity() : 0;
            if (numDevices <= 0) {
                throw new RuntimeException("No physical devices found");
            }

            // Populate available devices
            List<PhysicalDevice> devices = new ArrayList<>();
            for (int i = 0; i < numDevices; i++) {
                VkPhysicalDevice vkPhysicalDevice = new VkPhysicalDevice(pPhysicalDevices.get(i), instance.getVkInstance());
                PhysicalDevice physicalDevice = new PhysicalDevice(vkPhysicalDevice);

                String deviceName = physicalDevice.getDeviceName();
                if (physicalDevice.hasGraphicsQueueFamily() && physicalDevice.hasKHRSwapChainExtension()) {
                    System.out.printf("Device [%s] supports required extensions\n", deviceName);
                    if (preferredDeviceName != null && preferredDeviceName.equals(deviceName)) {
                        selectedPhysicalDevice = physicalDevice;
                        break;
                    }
                    devices.add(physicalDevice);
                } else {
                    System.out.printf("Device [%s] does not support required extensions\n", deviceName);
                    physicalDevice.cleanup();
                }
            }

            // No preferred device, or it does not meet requirements, just pick the first one
            selectedPhysicalDevice = selectedPhysicalDevice == null && !devices.isEmpty() ? devices.removeFirst() : selectedPhysicalDevice;

            // Clean up non-selected devices
            for (PhysicalDevice physicalDevice : devices) {
                physicalDevice.cleanup();
            }

            if (selectedPhysicalDevice == null) {
                throw new RuntimeException("No suitable physical devices found");
            }
            System.out.printf("Selected device: [%s]\n", selectedPhysicalDevice.getDeviceName());
        }

        return selectedPhysicalDevice;
    }

    protected static PointerBuffer getPhysicalDevices(Instance instance, MemoryStack stack) {
        PointerBuffer pPhysicalDevices;
        // Get number of physical devices
        IntBuffer intBuffer = stack.mallocInt(1);
        vkCheck(vkEnumeratePhysicalDevices(instance.getVkInstance(), intBuffer, null),
                "Failed to get number of physical devices");
        int numDevices = intBuffer.get(0);
        System.out.printf("Detected {%d} physical device(s)", numDevices);

        // Populate physical devices list pointer
        pPhysicalDevices = stack.mallocPointer(numDevices);
        vkCheck(vkEnumeratePhysicalDevices(instance.getVkInstance(), intBuffer, pPhysicalDevices),
                "Failed to get physical devices");
        return pPhysicalDevices;
    }


    public String getDeviceName() {
        return vkPhysicalDeviceProperties.deviceNameString();
    }

    public VkPhysicalDeviceMemoryProperties getVkMemoryProperties() {
        return vkMemoryProperties;
    }

    public VkPhysicalDevice getVkPhysicalDevice() {
        return vkPhysicalDevice;
    }

    public VkPhysicalDeviceFeatures getVkPhysicalDeviceFeatures() {
        return vkPhysicalDeviceFeatures;
    }

    public VkPhysicalDeviceProperties getVkPhysicalDeviceProperties() {
        return vkPhysicalDeviceProperties;
    }

    public VkQueueFamilyProperties.Buffer getVkQueueFamilyProps() {
        return vkQueueFamilyProps;
    }

    private boolean hasGraphicsQueueFamily() {
        boolean result = false;
        int numQueueFamilies = vkQueueFamilyProps != null ? vkQueueFamilyProps.capacity() : 0;
        for (int i = 0; i < numQueueFamilies; i++) {
            VkQueueFamilyProperties familyProps = vkQueueFamilyProps.get(i);
            if ((familyProps.queueFlags() & VK_QUEUE_GRAPHICS_BIT) != 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean hasKHRSwapChainExtension() {
        boolean result = false;
        int numExtensions = vkDeviceExtensions != null ? vkDeviceExtensions.capacity() : 0;
        for (int i = 0; i < numExtensions; i++) {
            String extensionName = vkDeviceExtensions.get(i).extensionNameString();
            if (KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME.equals(extensionName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void cleanup() {
        System.out.printf("Destroying physical device [%s]", vkPhysicalDeviceProperties.deviceNameString());
        vkMemoryProperties.free();
        vkPhysicalDeviceFeatures.free();
        vkQueueFamilyProps.free();
        vkDeviceExtensions.free();
        vkPhysicalDeviceProperties.free();
    }
}
