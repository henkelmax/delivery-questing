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
        xSize = 176;
        ySize = 133;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 9, FONT_COLOR);
        font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8F, (float) (ySize - 96 + 3), FONT_COLOR);
    }

    protected void drawCentered(MatrixStack matrixStack, ITextComponent text, int y, int color) {
        int width = font.getStringWidth(text.getString());
        font.func_243248_b(matrixStack, text, xSize / 2F - width / 2F, y, color);
    }

}