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

    public OfferEMail(Offer offer) {
        this.offerID = offer.getId();
        this.offer = offer;
    }

    public OfferEMail() {

    }

    @Override
    public IFormattableTextComponent getTitle() {
        return new TranslationTextComponent("message.delivery.now_available", getOffer().getItem().getDisplayName());
    }

    @Override
    public IFormattableTextComponent getText() {
        return new TranslationTextComponent("message.delivery.now_available_description", getOffer().getItem().getDisplayName());
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
        matrixStack.push();
        Minecraft.getInstance().getTextureManager().bindTexture(MINAZON_ICON);
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 16, 16, 16, 16);
        matrixStack.pop();
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
        compound.putUniqueId("OfferID", offerID);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        offerID = compound.getUniqueId("OfferID");
    }
}
