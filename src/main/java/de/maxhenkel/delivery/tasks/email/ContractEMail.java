package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

public class ContractEMail extends EMail {

    public static final ResourceLocation ACCEPTED_TASK = new ResourceLocation(Main.MODID, "textures/gui/computer/accepted_task.png");

    private UUID taskID;
    @Nullable
    private Task task;
    @Nullable
    private ResourceLocation icon;
    private boolean iconLoading;

    public ContractEMail(Group group, Task task) {
        super(group);
        this.taskID = task.getId();
        this.task = task;
    }

    public ContractEMail(Group group) {
        super(group);
    }

    @Override
    public boolean isValid() {
        return getTask() != null;
    }

    @Override
    public MutableComponent getTitle() {
        return new TextComponent(getTask().getName());
    }

    @Override
    public MutableComponent getText() {
        return new TextComponent(getTask().getDescription());
    }

    @Override
    public MutableComponent getSender() {
        return new TextComponent(getTask().getContractorName());
    }

    public UUID getTaskID() {
        return taskID;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(PoseStack matrixStack, @Nullable Group group) {
        if (icon == null && !iconLoading) {
            DummyPlayer.loadSkin(getTask().getSkin(), resourceLocation -> icon = resourceLocation);
            iconLoading = true;
            return;
        }

        if (icon != null) {
            matrixStack.pushPose();
            matrixStack.scale(2F, 2F, 1F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, icon);
            GuiComponent.blit(matrixStack, 0, 0, 8, 8, 8, 8, 64, 64);
            GuiComponent.blit(matrixStack, 0, 0, 40, 8, 8, 8, 64, 64);
            matrixStack.popPose();
            if (group != null && !group.canAcceptTask(taskID)) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                RenderSystem.setShaderTexture(0, ACCEPTED_TASK);
                GuiComponent.blit(matrixStack, 4, 4, 0, 0, 16, 16, 16, 16);
            }
        }
    }

    private Task getTask() {
        if (task == null) {
            task = Main.TASK_MANAGER.getTask(taskID, group);
        }
        return task;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = super.serializeNBT();
        compound.putUUID("TaskID", taskID);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        taskID = compound.getUUID("TaskID");
    }
}
