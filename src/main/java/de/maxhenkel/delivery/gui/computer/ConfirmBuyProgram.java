package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageBuyOffer;
import de.maxhenkel.delivery.sounds.ModSounds;
import de.maxhenkel.delivery.tasks.Offer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

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
    protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, BACKGROUND);
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

        mc.font.draw(matrixStack, new TranslatableComponent("message.delivery.minazon_url"), guiLeft + 5, guiTop + 16, 0);

        mc.font.draw(matrixStack, new TranslatableComponent("message.delivery.minazon_confirm_url"), guiLeft + 3 + 50 + 2, guiTop + 3 + 70 + 4, 0);

        MutableComponent buy = new TranslatableComponent("message.delivery.confirm_buy", new TranslatableComponent("message.delivery.item_amount", offer.getStack().getHoverName(), offer.getStack().getCount()), new TranslatableComponent("message.delivery.price", offer.getPrice()));
        List<FormattedCharSequence> list = mc.font.split(buy, 148 - 4);
        for (int i = 0; i < list.size(); i++) {
            FormattedCharSequence txt = list.get(i);
            mc.font.draw(matrixStack, txt, guiLeft + 3 + 53, guiTop + 3 + 86 + i * 10, 0);
        }

        screen.drawCentered(matrixStack, new TranslatableComponent("message.delivery.confirm"), guiLeft + confirm.getPosX() + confirm.getWidth() / 2, guiTop + confirm.getPosY() + confirm.getHeight() / 2 - 4, 0);
        screen.drawCentered(matrixStack, new TranslatableComponent("message.delivery.cancel"), guiLeft + cancel.getPosX() + cancel.getWidth() / 2, guiTop + cancel.getPosY() + cancel.getHeight() / 2 - 4, 0);
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
