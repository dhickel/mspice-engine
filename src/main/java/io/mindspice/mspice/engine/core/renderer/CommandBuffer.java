package io.mindspice.mspice.engine.core.renderer;

import io.mindspice.mspice.engine.core.engine.CleanUp;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import static org.lwjgl.vulkan.VK11.*;


public class CommandBuffer implements CleanUp {

    private final CommandPool commandPool;
    private final boolean oneTimeSubmit;
    private final VkCommandBuffer vkCommandBuffer;
    private boolean primary;

    /*
    Primary command buffers are submitted to queues for their execution and can contain several secondary command buffers.
    Secondary command buffers cannot be submitted directly to a queue, they always need to be included into a primary buffer.
     A use case for secondary buffers is command reuse. With secondary command buffers we can record some commands that may
     be shared between multiple primary command buffers.
     */

    public CommandBuffer(CommandPool commandPool, boolean primary, boolean oneTimeSubmit) {
        System.out.println("Creating command buffer");
        this.commandPool = commandPool;
        this.primary = primary;
        this.oneTimeSubmit = oneTimeSubmit;
        VkDevice vkDevice = commandPool.getDevice().getVkDevice();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferAllocateInfo cmdBufAllocateInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool.getVkCommandPool())
                    .level(primary ? VK_COMMAND_BUFFER_LEVEL_PRIMARY : VK_COMMAND_BUFFER_LEVEL_SECONDARY)
                    .commandBufferCount(1);
            PointerBuffer pb = stack.mallocPointer(1);
            VulkanUtils.vkCheck(vkAllocateCommandBuffers(vkDevice, cmdBufAllocateInfo, pb),
                    "Failed to allocate render command buffer");

            vkCommandBuffer = new VkCommandBuffer(pb.get(0), vkDevice);
        }
    }

    public void beginRecording() {
        beginRecording(null);
    }

    public void beginRecording(InheritanceInfo inheritanceInfo) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo cmdBufInfo = VkCommandBufferBeginInfo.calloc(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            if (oneTimeSubmit) {
                cmdBufInfo.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            }
            if (!primary) {
                if (inheritanceInfo == null) {
                    throw new RuntimeException("Secondary buffers must declare inheritance info");
                }
                VkCommandBufferInheritanceInfo vkInheritanceInfo = VkCommandBufferInheritanceInfo.calloc(stack)
                        .sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_INHERITANCE_INFO)
                        .renderPass(inheritanceInfo.vkRenderPass)
                        .subpass(inheritanceInfo.subPass)
                        .framebuffer(inheritanceInfo.vkFrameBuffer);
                cmdBufInfo.pInheritanceInfo(vkInheritanceInfo);
                cmdBufInfo.flags(VK_COMMAND_BUFFER_USAGE_RENDER_PASS_CONTINUE_BIT);
            }
            VulkanUtils.vkCheck(vkBeginCommandBuffer(vkCommandBuffer, cmdBufInfo), "Failed to begin command buffer");
        }
    }

    @Override
    public void cleanup() {
        System.out.println("Destroying command buffer");
        vkFreeCommandBuffers(commandPool.getDevice().getVkDevice(), commandPool.getVkCommandPool(),
                vkCommandBuffer);
    }

    public void endRecording() {
        VulkanUtils.vkCheck(vkEndCommandBuffer(vkCommandBuffer), "Failed to end command buffer");
    }

    public VkCommandBuffer getVkCommandBuffer() {
        return vkCommandBuffer;
    }

    public void reset() {
        vkResetCommandBuffer(vkCommandBuffer, VK_COMMAND_BUFFER_RESET_RELEASE_RESOURCES_BIT);
    }

    public record InheritanceInfo(long vkRenderPass, long vkFrameBuffer, int subPass) {
    }
}