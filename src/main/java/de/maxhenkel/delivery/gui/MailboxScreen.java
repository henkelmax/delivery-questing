package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MailboxScreen extends ScreenBase<MailboxContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/mailbox.png");

    private PlayerInventory playerInventory;

    public MailboxScreen(MailboxContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 145;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 6, FONT_COLOR);
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.inbox"), 42, 21, FONT_COLOR);
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.outbox"), 132, 21, FONT_COLOR);

        font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8, (float) (ySize - 96 + 3), FONT_COLOR);
    }

}