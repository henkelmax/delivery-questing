package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageMarkEMailRead;
import de.maxhenkel.delivery.tasks.email.ContractEMail;
import de.maxhenkel.delivery.tasks.email.EMail;
import de.maxhenkel.delivery.tasks.email.OfferEMail;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MailProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/mail.png");

    private int offset;
    private List<EMail> eMails;

    private ScreenBase.HoverArea[] hoverAreas;
    private ScreenBase.HoverArea close;

    public MailProgram(ComputerScreen screen) {
        super(screen);

        eMails = new ArrayList<>(screen.getContainer().getGroup().getEMails());
        Collections.reverse(eMails);
    }

    @Override
    protected void init() {
        super.init();

        hoverAreas = new ScreenBase.HoverArea[5];
        for (int i = 0; i < hoverAreas.length; i++) {
            hoverAreas[i] = new ScreenBase.HoverArea(3, 12 + i * 33, 239, 33);
        }

        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bindTexture(BACKGROUND);
        AbstractGui.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188, 512, 512);

        mc.fontRenderer.func_243248_b(matrixStack, new TranslationTextComponent("message.delivery.email"), guiLeft + 5, guiTop + 4, 0xFFFFFF);

        for (int i = offset; i < eMails.size() && i < offset + 5; i++) {
            mc.getTextureManager().bindTexture(BACKGROUND);
            int pos = i - offset;
            int startY = guiTop + 12 + pos * 33;
            EMail eMail = eMails.get(i);
            if (hoverAreas[pos].isHovered(guiLeft, guiTop, mouseX, mouseY)) {
                if (eMail.isRead()) {
                    AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 287, 239, 33, 512, 512);
                } else {
                    AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 221, 239, 33, 512, 512);
                }
            } else {
                if (eMail.isRead()) {
                    AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 254, 239, 33, 512, 512);
                } else {
                    AbstractGui.blit(matrixStack, guiLeft + 3, startY, 0, 188, 239, 33, 512, 512);
                }
            }

            matrixStack.push();
            matrixStack.translate(guiLeft + 11, startY + 8, 0F);
            eMail.renderIcon(matrixStack, getContainer().getGroup());
            matrixStack.pop();

            mc.fontRenderer.func_243248_b(matrixStack, eMail.getTitle(), guiLeft + 35, startY + 2, 0xFFFFFF);

            List<IReorderingProcessor> list = mc.fontRenderer.trimStringToWidth(eMail.getText(), hoverAreas[pos].getWidth() - 32); //TODO
            for (int m = 0; m < list.size() && m < 2; m++) {
                mc.fontRenderer.func_238422_b_(matrixStack, list.get(m), guiLeft + 35, startY + 12 + m * 10, 0);
            }
        }

        mc.getTextureManager().bindTexture(BACKGROUND);

        if (eMails.size() > 5) {
            float h = 165 - 27;
            float perc = (float) offset / (float) (eMails.size() - 5);
            int posY = guiTop + 12 + (int) (h * perc);
            AbstractGui.blit(matrixStack, guiLeft + xSize - 13, posY, 239, 188, 10, 27, 512, 512);
        } else {
            AbstractGui.blit(matrixStack, guiLeft + xSize - 13, guiTop + 12, 239, 215, 10, 27, 512, 512);
        }

        if (close.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            AbstractGui.blit(matrixStack, guiLeft + close.getPosX(), guiTop + close.getPosY(), 0, 320, close.getWidth(), close.getHeight(), 512, 512);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (eMails.size() > 5) {
            if (delta < 0D) {
                offset = Math.min(offset + 1, eMails.size() - 5);
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
            if (offset + i >= eMails.size()) {
                break;
            }
            if (!hoverAreas[i].isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
                continue;
            }
            EMail eMail = eMails.get(offset + i);
            if (!eMail.isRead()) {
                Main.SIMPLE_CHANNEL.sendToServer(new MessageMarkEMailRead(eMail.getId()));
                eMail.setRead(true);
            }
            if (eMail instanceof ContractEMail) {
                screen.setProgram(new ContractProgram(screen, this, ((ContractEMail) eMail).getTaskID()));
            } else if (eMail instanceof OfferEMail) {
                screen.setProgram(new OfferMailProgram(screen, this, (OfferEMail) eMail));
            }
            playClickSound();
            return true;
        }
        if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new DesktopProgram(screen));
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
