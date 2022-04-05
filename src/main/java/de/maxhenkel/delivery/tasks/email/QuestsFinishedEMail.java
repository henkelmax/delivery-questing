package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
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
    public MutableComponent getTitle() {
        return new TranslatableComponent("message.delivery.quests_finished");
    }

    @Override
    public MutableComponent getText() {
        return new TranslatableComponent("message.delivery.quests_finished_description");
    }

    @Override
    public MutableComponent getSender() {
        return new TranslatableComponent("message.delivery.unknown");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(PoseStack matrixStack, Group group) {
        matrixStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, ICON);
        GuiComponent.blit(matrixStack, 0, 0, 0, 0, 16, 16, 16, 16);
        matrixStack.popPose();
    }
}
