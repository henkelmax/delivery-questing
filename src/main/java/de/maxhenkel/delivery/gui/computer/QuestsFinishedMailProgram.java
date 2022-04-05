package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.email.QuestsFinishedEMail;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class QuestsFinishedMailProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/generic_mail.png");

    private MailProgram mailProgram;
    private ScreenBase.HoverArea close;
    private QuestsFinishedEMail eMail;

    public QuestsFinishedMailProgram(ComputerScreen screen, MailProgram mailProgram, QuestsFinishedEMail eMail) {
        super(screen);
        this.mailProgram = mailProgram;
        this.eMail = eMail;
    }

    @Override
    protected void init() {
        super.init();
        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);
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

        if (close.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + close.getPosX(), guiTop + close.getPosY(), 0, 188, close.getWidth(), close.getHeight());
        }

        Font font = mc.font;

        mc.font.draw(matrixStack, eMail.getTitle(), guiLeft + 5, guiTop + 4, 0xFFFFFF);

        screen.drawCentered(matrixStack, eMail.getTitle(), guiLeft + xSize / 2, guiTop + 3 + 9 + 3, 0);


        int yPos = guiTop + 3 + 9 + 3 + 15;

        List<FormattedCharSequence> list = font.split(eMail.getText(), xSize - 16);
        for (FormattedCharSequence text : list) {
            font.draw(matrixStack, text, guiLeft + 3 + 8, yPos, screen.FONT_COLOR);
            yPos += 10;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(mailProgram);
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
