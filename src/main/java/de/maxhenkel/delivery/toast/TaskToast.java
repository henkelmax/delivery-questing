package de.maxhenkel.delivery.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.Nullable;
import java.util.List;

public class TaskToast implements Toast {

    private Task task;
    private Component title;
    private Component subtitle;
    @Nullable
    private ResourceLocation skin;
    private boolean hasPlayedSound;
    private long showTime;
    private boolean playSound;

    public TaskToast(Task task, MutableComponent title, boolean playSound) {
        this.task = task;
        this.title = title.withStyle(ChatFormatting.YELLOW);
        this.playSound = playSound;
        this.subtitle = new TextComponent(task.getName()).withStyle(ChatFormatting.WHITE);
        DummyPlayer.loadSkin(task.getSkin(), resourceLocation -> {
            this.skin = resourceLocation;
        });
        this.showTime = 5000L;
    }

    @Override
    public Visibility render(PoseStack matrixStack, ToastComponent toastGui, long time) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        toastGui.blit(matrixStack, 0, 0, 0, 0, width(), height());

        List<FormattedCharSequence> list = toastGui.getMinecraft().font.split(subtitle, 125);
        toastGui.getMinecraft().font.draw(matrixStack, title, 30.0F, 7.0F, 0);
        if (list.size() > 0) {
            toastGui.getMinecraft().font.draw(matrixStack, list.get(0), 30.0F, 18.0F, 0);
        }

        if (playSound) {
            if (!hasPlayedSound && time > 0L) {
                hasPlayedSound = true;
                toastGui.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, 1F, 1F));
            }
        }

        if (skin != null) {
            matrixStack.pushPose();
            matrixStack.scale(2F, 2F, 1F);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, skin);
            GuiComponent.blit(matrixStack, 8 / 2, 8 / 2, 8, 8, 8, 8, 64, 64);
            GuiComponent.blit(matrixStack, 8 / 2, 8 / 2, 40, 8, 8, 8, 64, 64);
            matrixStack.popPose();
        }
        return time >= showTime ? Visibility.HIDE : Visibility.SHOW;

    }
}