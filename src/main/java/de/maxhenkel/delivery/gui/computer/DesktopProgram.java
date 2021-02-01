package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

public class DesktopProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/desktop.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Main.MODID, "textures/gui/computer/icons.png");
    private IFormattableTextComponent MINTERNET = new TranslationTextComponent("tooltip.delivery.minternet");
    private IFormattableTextComponent MAIL = new TranslationTextComponent("message.delivery.email");
    private IFormattableTextComponent NOTES = new TranslationTextComponent("message.delivery.notes");

    private ScreenBase.HoverArea minternet;
    private ScreenBase.HoverArea mail;
    private ScreenBase.HoverArea notes;

    public DesktopProgram(ComputerScreen screen) {
        super(screen);
    }

    @Override
    protected void init() {
        super.init();

        minternet = new ScreenBase.HoverArea(16, 16, 32, 32, () -> Collections.singletonList(MINTERNET.mergeStyle(TextFormatting.WHITE).func_241878_f()));
        addHoverArea(minternet);

        mail = new ScreenBase.HoverArea(64, 16, 32, 32, () -> Collections.singletonList(MAIL.mergeStyle(TextFormatting.WHITE).func_241878_f()));
        addHoverArea(mail);

        notes = new ScreenBase.HoverArea(112, 16, 32, 32, () -> Collections.singletonList(NOTES.mergeStyle(TextFormatting.WHITE).func_241878_f()));
        addHoverArea(notes);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        drawIcon(matrixStack, minternet, mouseX, mouseY, 0, 0, MINTERNET);
        drawIcon(matrixStack, mail, mouseX, mouseY, 0, 32, MAIL);
        drawIcon(matrixStack, notes, mouseX, mouseY, 0, 64, NOTES);

        drawCount(matrixStack, mail, getContainer().getGroup().getUnreadEMailCount());
        drawCount(matrixStack, notes, getContainer().getGroup().getActiveTasks().getTasks().size());

        screen.drawHoverAreas(matrixStack, mouseX, mouseY);
    }

    private void drawIcon(MatrixStack matrixStack, ScreenBase.HoverArea hoverArea, int mouseX, int mouseY, int xOffset, int yOffset, IFormattableTextComponent name) {
        mc.getTextureManager().bindTexture(ICONS);
        if (hoverArea.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, hoverArea.getPosX(), hoverArea.getPosY(), xOffset + hoverArea.getWidth(), yOffset, hoverArea.getWidth(), hoverArea.getHeight());
        }
        screen.blit(matrixStack, hoverArea.getPosX(), hoverArea.getPosY(), xOffset, yOffset, hoverArea.getWidth(), hoverArea.getHeight());
        screen.drawCentered(matrixStack, name, hoverArea.getPosX() + hoverArea.getWidth() / 2 + 1, hoverArea.getPosY() + hoverArea.getHeight() + 2, TextFormatting.DARK_GRAY.getColor());
        screen.drawCentered(matrixStack, name, hoverArea.getPosX() + hoverArea.getWidth() / 2, hoverArea.getPosY() + hoverArea.getHeight() + 1, TextFormatting.WHITE.getColor());
    }

    private void drawCount(MatrixStack matrixStack, ScreenBase.HoverArea hoverArea, int count) {
        if (count > 0) {
            IFormattableTextComponent num = new StringTextComponent(String.valueOf(count)).mergeStyle(TextFormatting.DARK_RED);
            int w = mc.fontRenderer.getStringPropertyWidth(num);
            mc.fontRenderer.func_243248_b(matrixStack, num, hoverArea.getPosX() + hoverArea.getWidth() - 1 - w, hoverArea.getPosY() + 4, 0);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bindTexture(BACKGROUND);
        screen.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (minternet.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new MinazonProgram(screen, this));
            playClickSound();
            return true;
        }
        if (mail.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new MailProgram(screen, this));
            playClickSound();
            return true;
        }
        if (notes.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new NotesProgram(screen, this));
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
