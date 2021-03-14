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
        imageWidth = 176;
        imageHeight = 159;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 6, FONT_COLOR);

        long nextEmptying = getNextEmptying();
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.next_emptying", String.format("%02d", nextEmptying / 1000), String.format("%02d", (int) ((float) (nextEmptying % 1000) * 0.06F))), 20, FONT_COLOR);

        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.inbox"), 42, 35, FONT_COLOR);
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.outbox"), 132, 35, FONT_COLOR);

        font.draw(matrixStack, playerInventory.getDisplayName(), 8, (float) (imageHeight - 96 + 3), FONT_COLOR);
    }

    public long getNextEmptying() {
        long time = minecraft.level.getDayTime() % 24000;
        if (time <= 20) {
            time += 24000;
        }
        return 24020 - time;
    }

}