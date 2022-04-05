package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public abstract class CardboardBoxScreen extends ScreenBase<CardboardBoxContainer> {

    private Inventory playerInventory;

    public CardboardBoxScreen(ResourceLocation texture, CardboardBoxContainer container, Inventory playerInventory, Component name) {
        super(texture, container, playerInventory, name);
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