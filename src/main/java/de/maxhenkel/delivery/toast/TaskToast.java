package de.maxhenkel.delivery.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.List;

public class TaskToast implements IToast {

    private Task task;
    private ITextComponent title;
    private ITextComponent subtitle;
    @Nullable
    private ResourceLocation skin;
    private boolean hasPlayedSound;
    private long showTime;
    private boolean playSound;

    public TaskToast(Task task, IFormattableTextComponent title, boolean playSound) {
        this.task = task;
        this.title = title.withStyle(TextFormatting.YELLOW);
        this.playSound = playSound;
        this.subtitle = new StringTextComponent(task.getName()).withStyle(TextFormatting.WHITE);
        DummyPlayer.loadSkin(task.getSkin(), resourceLocation -> {
            this.skin = resourceLocation;
        });
        this.showTime = 5000L;
    }

    @Override
    public Visibility render(MatrixStack matrixStack, ToastGui toastGui, long time) {
        toastGui.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color3f(1F, 1F, 1F);
        toastGui.blit(matrixStack, 0, 0, 0, 0, width(), height());

        List<IReorderingProcessor> list = toastGui.getMinecraft().font.split(subtitle, 125);
        toastGui.getMinecraft().font.draw(matrixStack, title, 30.0F, 7.0F, 0);
        if (list.size() > 0) {
            toastGui.getMinecraft().font.draw(matrixStack, list.get(0), 30.0F, 18.0F, 0);
        }

        if (playSound) {
            if (!hasPlayedSound && time > 0L) {
                hasPlayedSound = true;
                toastGui.getMinecraft().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1F));
            }
        }

        if (skin != null) {
            matrixStack.pushPose();
            matrixStack.scale(2F, 2F, 1F);
            toastGui.getMinecraft().getTextureManager().bind(skin);
            AbstractGui.blit(matrixStack, 8 / 2, 8 / 2, 8, 8, 8, 8, 64, 64);
            AbstractGui.blit(matrixStack, 8 / 2, 8 / 2, 40, 8, 8, 8, 64, 64);
            matrixStack.popPose();
        }
        return time >= showTime ? Visibility.HIDE : Visibility.SHOW;

    }
}