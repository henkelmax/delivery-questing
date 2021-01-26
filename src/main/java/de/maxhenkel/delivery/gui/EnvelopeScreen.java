package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class EnvelopeScreen extends ScreenBase<EnvelopeContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/envelope.png");

    private PlayerInventory playerInventory;

    public EnvelopeScreen(EnvelopeContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
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
