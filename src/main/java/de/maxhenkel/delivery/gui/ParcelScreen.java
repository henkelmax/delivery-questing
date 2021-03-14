package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ParcelScreen extends ScreenBase<ParcelContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/parcel.png");

    private PlayerInventory playerInventory;

    public ParcelScreen(ParcelContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
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
