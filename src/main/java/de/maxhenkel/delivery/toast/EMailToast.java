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
        this.title = new TranslationTextComponent("message.delivery.new_mail").mergeStyle(TextFormatting.WHITE);
        this.subtitle = eMail.getTitle().mergeStyle(TextFormatting.WHITE);
        this.showTime = 5000L;
    }

    public Visibility func_230444_a_(MatrixStack matrixStack, ToastGui toastGui, long time) {
        toastGui.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
        RenderSystem.color3f(1F, 1F, 1F);
        toastGui.blit(matrixStack, 0, 0, 0, 0, func_230445_a_(), func_238540_d_());

        List<IReorderingProcessor> list = toastGui.getMinecraft().fontRenderer.trimStringToWidth(subtitle, 125);
        toastGui.getMinecraft().fontRenderer.func_243248_b(matrixStack, title, 30F, 7F, 0);
        if (list.size() > 0) {
            toastGui.getMinecraft().fontRenderer.func_238422_b_(matrixStack, list.get(0), 30F, 18F, 0);
        }
        matrixStack.push();
        matrixStack.translate(8D, 8D, 0D);
        eMail.renderIcon(matrixStack, null);
        matrixStack.pop();

        return time >= showTime ? Visibility.HIDE : Visibility.SHOW;
    }
}