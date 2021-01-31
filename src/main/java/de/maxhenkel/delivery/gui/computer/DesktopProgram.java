package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.sounds.ModSounds;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

public class DesktopProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/desktop.png");
    public static final ResourceLocation ICONS = new ResourceLocation(Main.MODID, "textures/gui/computer/icons.png");
    private IFormattableTextComponent INTERNET = new TranslationTextComponent("tooltip.delivery.internet");

    private ScreenBase.HoverArea internet;

    public DesktopProgram(ComputerScreen screen) {
        super(screen);
    }

    @Override
    protected void init() {
        super.init();

        internet = new ScreenBase.HoverArea(16, 16, 32, 32, () -> Collections.singletonList(INTERNET.mergeStyle(TextFormatting.WHITE).func_241878_f()));
        addHoverArea(internet);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        mc.getTextureManager().bindTexture(ICONS);

        drawIcon(matrixStack, internet, mouseX, mouseY, 0, 0);

        screen.drawHoverAreas(matrixStack, mouseX, mouseY);
    }

    private void drawIcon(MatrixStack matrixStack, ScreenBase.HoverArea hoverArea, int mouseX, int mouseY, int xOffset, int yOffset) {
        if (hoverArea.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, hoverArea.getPosX(), hoverArea.getPosY(), xOffset + hoverArea.getWidth(), yOffset, hoverArea.getWidth(), hoverArea.getHeight());
        }
        screen.blit(matrixStack, hoverArea.getPosX(), hoverArea.getPosY(), xOffset, yOffset, hoverArea.getWidth(), hoverArea.getHeight());
        screen.drawCentered(matrixStack, INTERNET, hoverArea.getPosX() + hoverArea.getWidth() / 2 + 1, hoverArea.getPosY() + hoverArea.getHeight() + 1, TextFormatting.DARK_GRAY.getColor());
        screen.drawCentered(matrixStack, INTERNET, hoverArea.getPosX() + hoverArea.getWidth() / 2, hoverArea.getPosY() + hoverArea.getHeight(), TextFormatting.WHITE.getColor());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bindTexture(BACKGROUND);
        screen.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (internet.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(new MinazonProgram(screen));
            mc.getSoundHandler().play(SimpleSound.master(ModSounds.MOUSE_CLICK, 1F));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
