package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageBuyOffer;
import de.maxhenkel.delivery.sounds.ModSounds;
import de.maxhenkel.delivery.tasks.Offer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class ConfirmBuyProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/confirm_buy.png");

    private ScreenBase.HoverArea confirm;
    private ScreenBase.HoverArea cancel;
    private ScreenBase.HoverArea close;
    private ScreenBase.HoverArea closeDialog;
    private Offer offer;
    private ComputerProgram parent;

    public ConfirmBuyProgram(ComputerScreen screen, ComputerProgram parent, Offer offer) {
        super(screen);
        this.parent = parent;
        this.offer = offer;
    }

    @Override
    protected void init() {
        super.init();

        confirm = new ScreenBase.HoverArea(56, 132, 53, 18);
        cancel = new ScreenBase.HoverArea(xSize - 56 - 53, 132, 53, 18);
        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);
        closeDialog = new ScreenBase.HoverArea(3 + 191, 3 + 61, 9, 9);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bind(BACKGROUND);
        screen.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188);

        if (confirm.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + confirm.getPosX(), guiTop + confirm.getPosY(), 0, 206, confirm.getWidth(), confirm.getHeight());
        } else {
            screen.blit(matrixStack, guiLeft + confirm.getPosX(), guiTop + confirm.getPosY(), 0, 188, confirm.getWidth(), confirm.getHeight());
        }

        if (cancel.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + cancel.getPosX(), guiTop + cancel.getPosY(), 0, 206, cancel.getWidth(), cancel.getHeight());
        } else {
            screen.blit(matrixStack, guiLeft + cancel.getPosX(), guiTop + cancel.getPosY(), 0, 188, cancel.getWidth(), cancel.getHeight());
        }

        if (close.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + close.getPosX(), guiTop + close.getPosY(), 0, 224, close.getWidth(), close.getHeight());
        }
        if (closeDialog.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + closeDialog.getPosX(), guiTop + closeDialog.getPosY(), 0, 224, closeDialog.getWidth(), closeDialog.getHeight());
        }

        mc.font.draw(matrixStack, new TranslationTextComponent("message.delivery.minazon_url"), guiLeft + 5, guiTop + 16, 0);

        mc.font.draw(matrixStack, new TranslationTextComponent("message.delivery.minazon_confirm_url"), guiLeft + 3 + 50 + 2, guiTop + 3 + 70 + 4, 0);

        IFormattableTextComponent buy = new TranslationTextComponent("message.delivery.confirm_buy", new TranslationTextComponent("message.delivery.item_amount", offer.getStack().getHoverName(), offer.getStack().getCount()), new TranslationTextComponent("message.delivery.price", offer.getPrice()));
        List<IReorderingProcessor> list = mc.font.split(buy, 148 - 4);
        for (int i = 0; i < list.size(); i++) {
            IReorderingProcessor txt = list.get(i);
            mc.font.draw(matrixStack, txt, guiLeft + 3 + 53, guiTop + 3 + 86 + i * 10, 0);
        }

        screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.confirm"), guiLeft + confirm.getPosX() + confirm.getWidth() / 2, guiTop + confirm.getPosY() + confirm.getHeight() / 2 - 4, 0);
        screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.cancel"), guiLeft + cancel.getPosX() + cancel.getWidth() / 2, guiTop + cancel.getPosY() + cancel.getHeight() / 2 - 4, 0);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (confirm.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageBuyOffer(offer.getId()));
            screen.setProgram(parent);
            playSound(ModSounds.CASH_REGISTER);
            return true;
        } else if (cancel.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(parent);
            playClickSound();
            return true;
        } else if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new DesktopProgram(screen));
            playClickSound();
            return true;
        } else if (closeDialog.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(parent);
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
