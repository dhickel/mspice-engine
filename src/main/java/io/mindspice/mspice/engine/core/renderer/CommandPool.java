package io.mindspice.mspice.engine.core.renderer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;

import java.nio.LongBuffer;

import static org.lwjgl.vulkan.VK11.*;


    /*
    flags: Specifies the behavior of the command pool. In our case we use the VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT
    which indicates that the commands can be reset individually. Resetting allows reusing command buffers after they have been
    recorded. This allows us to re-record them. If this is not enabled, we would need to create new commands and free the ones
    that could not be reused any more. For simplicity, we will enable that flag, but if you pre-record the command buffers you
    should disable this for performance. Even if you need to re-record command buffers and be top performant, it is recommended
    to reset the pool as whole. If you opt for this approach, yuo should create one command pool per frame buffer image in order
    to avoid resetting the pool while commands are in use. In this case, commands should be created as a one time submit.
     */


public class CommandPool {

    private final Device device;
    private final long vkCommandPool;

    public CommandPool(Device device, int queueFamilyIndex) {
        System.out.println("Creating Vulkan CommandPool");

        this.device = device;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                    .queueFamilyIndex(queueFamilyIndex);

            LongBuffer lp = stack.mallocLong(1);
            VulkanUtils.vkCheck(vkCreateCommandPool(device.getVkDevice(), cmdPoolInfo, null, lp),
                    "Failed to create command pool");

            vkCommandPool = lp.get(0);
        }
    }

    public void cleanup() {
        vkDestroyCommandPool(device.getVkDevice(), vkCommandPool, null);
    }

    public Device getDevice() {
        return device;
    }

    public long getVkCommandPool() {
        return vkCommandPool;
    }
}