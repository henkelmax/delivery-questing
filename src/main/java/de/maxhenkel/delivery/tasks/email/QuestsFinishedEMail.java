package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class QuestsFinishedEMail extends EMail {

    public static final ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/gui/computer/trophy.png");

    public QuestsFinishedEMail(Group group) {
        super(group);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public IFormattableTextComponent getTitle() {
        return new TranslationTextComponent("message.delivery.quests_finished");
    }

    @Override
    public IFormattableTextComponent getText() {
        return new TranslationTextComponent("message.delivery.quests_finished_description");
    }

    @Override
    public IFormattableTextComponent getSender() {
        return new TranslationTextComponent("message.delivery.unknown");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(MatrixStack matrixStack, Group group) {
        matrixStack.push();
        Minecraft.getInstance().getTextureManager().bindTexture(ICON);
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 16, 16, 16, 16);
        matrixStack.pop();
    }
}
