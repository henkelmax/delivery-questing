package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Offer;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

public class OfferEMail extends EMail {

    public static final ResourceLocation MINAZON_ICON = new ResourceLocation(Main.MODID, "textures/gui/computer/minazon_icon.png");

    private UUID offerID;
    @Nullable
    private Offer offer;

    public OfferEMail(Group group, Offer offer) {
        super(group);
        this.offerID = offer.getId();
        this.offer = offer;
    }

    public OfferEMail(Group group) {
        super(group);
    }

    @Override
    public boolean isValid() {
        return getOffer() != null;
    }

    @Override
    public MutableComponent getTitle() {
        return new TranslatableComponent("message.delivery.now_available", getOffer().getStack().getHoverName());
    }

    @Override
    public MutableComponent getText() {
        return new TranslatableComponent("message.delivery.now_available_description", getOffer().getStack().getHoverName());
    }

    @Override
    public MutableComponent getSender() {
        return new TranslatableComponent("message.delivery.minazon");
    }

    public UUID getOfferID() {
        return offerID;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(PoseStack matrixStack, Group group) {
        matrixStack.pushPose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, MINAZON_ICON);
        GuiComponent.blit(matrixStack, 0, 0, 0, 0, 16, 16, 16, 16);
        matrixStack.popPose();
    }

    public Offer getOffer() {
        if (offer == null) {
            offer = Main.OFFER_MANAGER.getOffer(offerID);
        }
        return offer;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = super.serializeNBT();
        compound.putUUID("OfferID", offerID);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        super.deserializeNBT(compound);
        offerID = compound.getUUID("OfferID");
    }
}
