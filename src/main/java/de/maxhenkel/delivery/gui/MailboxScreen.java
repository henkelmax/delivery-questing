package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MailboxScreen extends ScreenBase<MailboxContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/mailbox.png");

    private PlayerInventory playerInventory;

    public MailboxScreen(MailboxContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 164;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.inbox"), 9, FONT_COLOR);
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.outbox"), 40, FONT_COLOR);
        font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8F, (float) (ySize - 96 + 3), FONT_COLOR);
    }

    protected void drawCentered(MatrixStack matrixStack, IFormattableTextComponent text, int y, int color) {
        int width = font.getStringPropertyWidth(text);
        font.func_243248_b(matrixStack, text, xSize / 2F - width / 2F, y, color);
    }

}