package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class CardboardBoxScreen extends ScreenBase<CardboardBoxContainer> {

    private PlayerInventory playerInventory;

    public CardboardBoxScreen(ResourceLocation texture, CardboardBoxContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(texture, container, playerInventory, name);
        this.playerInventory = playerInventory;

        imageWidth = 176;
        imageHeight = 133;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 9, FONT_COLOR);
        font.draw(matrixStack, playerInventory.getDisplayName(), 8F, (float) (imageHeight - 96 + 3), FONT_COLOR);
    }

}