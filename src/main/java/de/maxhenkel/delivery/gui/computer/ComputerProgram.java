package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;

public abstract class ComputerProgram {

    protected ComputerScreen screen;
    protected Minecraft mc;
    protected int width, height, guiLeft, guiTop, xSize, ySize;

    public ComputerProgram(ComputerScreen screen) {
        this.screen = screen;
        mc = Minecraft.getInstance();
    }

    protected void init() {
        width = screen.width;
        height = screen.height;
        guiLeft = screen.getGuiLeft();
        guiTop = screen.getGuiTop();
        xSize = screen.getXSize();
        ySize = screen.getYSize();
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {

    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {

    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return false;
    }

    protected void addWidget(Widget widget) {
        screen.addWidget(widget);
    }

    public ComputerContainer getContainer() {
        return screen.getContainer();
    }

    public void addHoverArea(ScreenBase.HoverArea hoverArea) {
        screen.addHoverArea(hoverArea);
    }


}
