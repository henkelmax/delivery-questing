package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Offer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
    public IFormattableTextComponent getTitle() {
        return new TranslationTextComponent("message.delivery.now_available", getOffer().getStack().getHoverName());
    }

    @Override
    public IFormattableTextComponent getText() {
        return new TranslationTextComponent("message.delivery.now_available_description", getOffer().getStack().getHoverName());
    }

    @Override
    public IFormattableTextComponent getSender() {
        return new TranslationTextComponent("message.delivery.minazon");
    }

    public UUID getOfferID() {
        return offerID;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderIcon(MatrixStack matrixStack, Group group) {
        matrixStack.pushPose();
        Minecraft.getInstance().getTextureManager().bind(MINAZON_ICON);
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 16, 16, 16, 16);
        matrixStack.popPose();
    }

    public Offer getOffer() {
        if (offer == null) {
            offer = Main.OFFER_MANAGER.getOffer(offerID);
        }
        return offer;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = super.serializeNBT();
        compound.putUUID("OfferID", offerID);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        offerID = compound.getUUID("OfferID");
    }
}
