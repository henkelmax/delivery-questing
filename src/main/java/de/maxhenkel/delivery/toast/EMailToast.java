package de.maxhenkel.delivery.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.tasks.email.EMail;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class EMailToast implements Toast {

    private EMail eMail;
    private Component title;
    private Component subtitle;
    private long showTime;

    public EMailToast(EMail eMail) {
        this.eMail = eMail;
        this.title = new TranslatableComponent("message.delivery.new_mail").withStyle(ChatFormatting.WHITE);
        this.subtitle = eMail.getTitle().withStyle(ChatFormatting.WHITE);
        this.showTime = 5000L;
    }

    @Override
    public Visibility render(PoseStack matrixStack, ToastComponent toastGui, long time) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        toastGui.blit(matrixStack, 0, 0, 0, 0, width(), height());

        List<FormattedCharSequence> list = toastGui.getMinecraft().font.split(subtitle, 125);
        toastGui.getMinecraft().font.draw(matrixStack, title, 30F, 7F, 0);
        if (list.size() > 0) {
            toastGui.getMinecraft().font.draw(matrixStack, list.get(0), 30F, 18F, 0);
        }
        matrixStack.pushPose();
        matrixStack.translate(8D, 8D, 0D);
        eMail.renderIcon(matrixStack, null);
        matrixStack.popPose();

        return time >= showTime ? Visibility.HIDE : Visibility.SHOW;
    }
}