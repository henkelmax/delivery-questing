package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ParcelScreen extends ScreenBase<ParcelContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/parcel.png");

    private Inventory playerInventory;

    public ParcelScreen(ParcelContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;

        imageWidth = 176;
        imageHeight = 133;
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 9, FONT_COLOR);
        font.draw(matrixStack, playerInventory.getDisplayName(), 8F, (float) (imageHeight - 96 + 3), FONT_COLOR);
    }
}
