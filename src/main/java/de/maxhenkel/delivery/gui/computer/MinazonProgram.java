package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Offer;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MinazonProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/minazon.png");

    private int offset;
    private List<Offer> offers;

    private ScreenBase.HoverArea[] hoverAreas;
    private ScreenBase.HoverArea close;

    private ComputerProgram parent;

    public MinazonProgram(ComputerScreen screen, ComputerProgram parent) {
        super(screen);
        this.parent = parent;

        List<Offer> o = Main.OFFER_MANAGER.getOffers();

        if (o == null) {
            screen.setProgram(new DesktopProgram(screen));
            return;
        }

        offers = o.stream().sorted(Comparator.comparingInt(Offer::getLevelRequirement)).collect(Collectors.toList());
    }

    @Override
    protected void init() {
        super.init();

        hoverAreas = new ScreenBase.HoverArea[5];
        for (int i = 0; i < hoverAreas.length; i++) {
            hoverAreas[i] = new ScreenBase.HoverArea(3, 26 + i * 33, 239, 33);
        }

        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        for (int i = 0; i < hoverAreas.length; i++) {
            if (offset + i >= offers.size()) {
                break;
            }
            if (!hoverAreas[i].isHovered(guiLeft, guiTop, mouseX, mouseY)) {
                continue;
            }
            Offer offer = offers.get(offset + i);
            List<ITextComponent> tooltipFromItem = mc.screen.getTooltipFromItem(offer.getStack());
            tooltipFromItem.set(0, new TranslationTextComponent("message.delivery.item_amount", tooltipFromItem.get(0), offer.getStack().getCount()));
            tooltipFromItem.add(1, new TranslationTextComponent("message.delivery.price", offer.getPrice()).withStyle(TextFormatting.GRAY));
            if (offer.isForEveryMember()) {
                tooltipFromItem.add(2, new TranslationTextComponent("message.delivery.for_every_member").withStyle(TextFormatting.DARK_GREEN));
            }
            if (offer.getPrice() > getContainer().getBalance()) {
                tooltipFromItem.add(new TranslationTextComponent("message.delivery.insufficient_balance").withStyle(TextFormatting.DARK_RED));
            }
            if (offer.getLevelRequirement() > getContainer().getLevel()) {
                tooltipFromItem.add(new TranslationTextComponent("message.delivery.insufficient_level").withStyle(TextFormatting.DARK_RED));
            }
            mc.screen.renderWrappedToolTip(matrixStack, tooltipFromItem, mouseX - guiLeft, mouseY - guiTop, mc.font);
            break;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bind(BACKGROUND);
        AbstractGui.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188, 512, 512);

        AbstractGui.blit(matrixStack, 3, 3, 0, 287, 56, 32, 512, 512);
        screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.balance").withStyle(TextFormatting.DARK_GRAY), 3 + 56 / 2, 9, 0);
        screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.price", getContainer().getBalance()).withStyle(TextFormatting.DARK_GREEN), 3 + 56 / 2, 21, 0);

        mc.font.draw(matrixStack, new TranslationTextComponent("message.delivery.minazon"), guiLeft + 5, guiTop + 4, 0xFFFFFF);
        mc.font.draw(matrixStack, new TranslationTextComponent("message.delivery.minazon_url"), guiLeft + 5, guiTop + 16, 0);

        for (int i = offset; i < offers.size() && i < offset + 5; i++) {
            mc.getTextureManager().bind(BACKGROUND);
            int pos = i - offset;
            int startY = guiTop + 26 + pos * 33;
            Offer offer = offers.get(i);
            if (offer.getLevelRequirement() > screen.getMenu().getLevel()) {
                AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 254, 239, 33, 512, 512);
            } else if (hoverAreas[pos].isHovered(guiLeft, guiTop, mouseX, mouseY)) {
                AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 221, 239, 33, 512, 512);
            } else {
                AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 188, 239, 33, 512, 512);
            }

            mc.getItemRenderer().renderAndDecorateItem(mc.player, offer.getStack(), guiLeft + 10, startY + 8);
            mc.getItemRenderer().renderGuiItemDecorations(mc.font, offer.getStack(), guiLeft + 10, startY + 8, null);

            IFormattableTextComponent price = new TranslationTextComponent("message.delivery.price", offer.getPrice());

            if (getContainer().getBalance() < offer.getPrice()) {
                price = price.withStyle(TextFormatting.DARK_RED);
            }

            mc.font.draw(matrixStack, price, guiLeft + 35, startY + 13, 0xFFFFFF);
            if (offer.getLevelRequirement() > screen.getMenu().getLevel()) {
                IFormattableTextComponent lvl = new TranslationTextComponent("message.delivery.level_required", offer.getLevelRequirement()).withStyle(TextFormatting.DARK_RED);
                int w = mc.font.width(lvl);
                mc.font.draw(matrixStack, lvl, guiLeft + xSize - w - 22, startY + 13, 0);
            }
        }

        mc.getTextureManager().bind(BACKGROUND);

        if (offers.size() > 5) {
            float h = 165 - 27;
            float perc = (float) offset / (float) (offers.size() - 5);
            int posY = guiTop + 26 + (int) (h * perc);
            AbstractGui.blit(matrixStack, guiLeft + xSize - 13, posY, 239, 188, 10, 27, 512, 512);
        } else {
            AbstractGui.blit(matrixStack, guiLeft + xSize - 13, guiTop + 26, 239, 215, 10, 27, 512, 512);
        }

        if (close.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            AbstractGui.blit(matrixStack, guiLeft + close.getPosX(), guiTop + close.getPosY(), 0, 319, close.getWidth(), close.getHeight(), 512, 512);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (offers.size() > 5) {
            if (delta < 0D) {
                offset = Math.min(offset + 1, offers.size() - 5);
            } else {
                offset = Math.max(offset - 1, 0);
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < hoverAreas.length; i++) {
            if (offset + i >= offers.size()) {
                break;
            }
            if (!hoverAreas[i].isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
                continue;
            }
            Offer offer = offers.get(offset + i);
            if (offer.getLevelRequirement() > getContainer().getLevel() || offer.getPrice() > getContainer().getBalance()) {
                break;
            }
            screen.setProgram(new ConfirmBuyProgram(screen, this, offer));
            playClickSound();
            return true;
        }
        if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(parent);
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
