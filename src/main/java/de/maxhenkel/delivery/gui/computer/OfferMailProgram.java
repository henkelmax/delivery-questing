package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.email.OfferEMail;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OfferMailProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/generic_mail.png");

    private OfferEMail offerEMail;
    private MailProgram mailProgram;
    private ScreenBase.HoverArea close;
    private ScreenBase.HoverArea item;
    private Button openMinazon;

    public OfferMailProgram(ComputerScreen screen, MailProgram mailProgram, OfferEMail offer) {
        super(screen);
        this.mailProgram = mailProgram;
        this.offerEMail = offer;

        if (offerEMail.getOffer() == null) {
            screen.setProgram(mailProgram);
        }
    }

    @Override
    protected void init() {
        super.init();
        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);
        item = new ScreenBase.HoverArea(xSize / 2 - 8, ySize - 3 - 3 - 8 - 20 - 32, 16, 16);

        openMinazon = new Button(guiLeft + xSize / 2 - 50, guiTop + ySize - 3 - 3 - 8 - 20, 100, 20, new TranslatableComponent("message.delivery.view"), button -> {
            screen.setProgram(new MinazonProgram(screen, this));
        });
        addWidget(openMinazon);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        ItemStack i = offerEMail.getOffer().getStack();
        mc.getItemRenderer().renderAndDecorateItem(i, this.item.getPosX(), this.item.getPosY());

        if (this.item.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            List<Component> tooltip = screen.getTooltipFromItem(i);
            screen.renderComponentTooltip(matrixStack, tooltip, mouseX - guiLeft, mouseY - guiTop, mc.font);
        }
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

        mc.font.draw(matrixStack, offerEMail.getTitle(), guiLeft + 5, guiTop + 4, 0xFFFFFF);

        screen.drawCentered(matrixStack, offerEMail.getTitle(), guiLeft + xSize / 2, guiTop + 3 + 9 + 3, 0);


        int yPos = guiTop + 3 + 9 + 3 + 15;

        List<FormattedCharSequence> list = font.split(offerEMail.getText(), xSize - 16);
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
