package de.maxhenkel.delivery.toast;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.delivery.tasks.email.EMail;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class EMailToast implements IToast {

    private EMail eMail;
    private ITextComponent title;
    private ITextComponent subtitle;
    private long showTime;

    public EMailToast(EMail eMail) {
        this.eMail = eMail;
        this.title = new TranslationTextComponent("message.delivery.new_mail").withStyle(TextFormatting.WHITE);
        this.subtitle = eMail.getTitle().withStyle(TextFormatting.WHITE);
        this.showTime = 5000L;
    }

    @Override
    public Visibility render(MatrixStack matrixStack, ToastGui toastGui, long time) {
        toastGui.getMinecraft().getTextureManager().bind(TEXTURE);
        RenderSystem.color3f(1F, 1F, 1F);
        toastGui.blit(matrixStack, 0, 0, 0, 0, width(), height());

        List<IReorderingProcessor> list = toastGui.getMinecraft().font.split(subtitle, 125);
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