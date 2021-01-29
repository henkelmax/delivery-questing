package de.maxhenkel.delivery.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;

public class TaskCompletedToast implements IToast {

    private Task task;
    private ITextComponent title;
    private ITextComponent subtitle;
    @Nullable
    private ResourceLocation skin;
    private boolean hasPlayedSound;
    private long showTime;

    public TaskCompletedToast(Task task) {
        this.task = task;
        this.title = new TranslationTextComponent("toast.delivery.contract_completed").mergeStyle(TextFormatting.YELLOW);
        this.subtitle = new StringTextComponent(task.getName()).mergeStyle(TextFormatting.WHITE);
        DummyPlayer.loadSkin(task.getSkin(), resourceLocation -> {
            this.skin = resourceLocation;
        });
        this.showTime = 5000L;
    }

    public IToast.Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long time) {
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1F, 1F, 1F);
        toastGui.blit(matrixStack, 0, 0, 0, 0, func_230445_a_(), func_238540_d_());

        List<IReorderingProcessor> list = toastGui.getMinecraft().fontRenderer.trimStringToWidth(subtitle, 125);
        toastGui.getMinecraft().fontRenderer.func_243248_b(matrixStack, title, 30.0F, 7.0F, 0);
        if (list.size() > 0) {
            toastGui.getMinecraft().fontRenderer.func_238422_b_(matrixStack, list.get(0), 30.0F, 18.0F, 0);
        }

        if (!hasPlayedSound && time > 0L) {
            hasPlayedSound = true;
            toastGui.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1F));
        }

        if (skin != null) {
            matrixStack.push();
            matrixStack.scale(2F, 2F, 1F);
            toastGui.getMinecraft().getTextureManager().bindTexture(skin);
            toastGui.blit(matrixStack, 8 / 2, 8 / 2, 8, 8, 8, 8, 64, 64);
            matrixStack.pop();
        }
        return time >= showTime ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;

    }
}