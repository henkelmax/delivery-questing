package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
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
    public IFormattableTextComponent getTitle() {
        return new StringTextComponent(getTask().getName());
    }

    @Override
    public IFormattableTextComponent getText() {
        return new StringTextComponent(getTask().getDescription());
    }

    @Override
    public IFormattableTextComponent getSender() {
        return new StringTextComponent(getTask().getContractorName());
    }

    public UUID getTaskID() {
        return taskID;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(MatrixStack matrixStack, @Nullable Group group) {
        if (icon == null && !iconLoading) {
            DummyPlayer.loadSkin(getTask().getSkin(), resourceLocation -> icon = resourceLocation);
            iconLoading = true;
            return;
        }

        if (icon != null) {
            matrixStack.pushPose();
            matrixStack.scale(2F, 2F, 1F);
            Minecraft.getInstance().getTextureManager().bind(icon);
            AbstractGui.blit(matrixStack, 0, 0, 8, 8, 8, 8, 64, 64);
            AbstractGui.blit(matrixStack, 0, 0, 40, 8, 8, 8, 64, 64);
            matrixStack.popPose();
            if (group != null && !group.canAcceptTask(taskID)) {
                Minecraft.getInstance().getTextureManager().bind(ACCEPTED_TASK);
                AbstractGui.blit(matrixStack, 4, 4, 0, 0, 16, 16, 16, 16);
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
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.putUUID("TaskID", taskID);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        taskID = compound.getUUID("TaskID");
    }
}
