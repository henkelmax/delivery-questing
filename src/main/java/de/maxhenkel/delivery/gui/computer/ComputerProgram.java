package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.sounds.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;

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

    protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {

    }

    protected void drawGuiContainerBackgroundLayer(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {

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

    public <T extends GuiEventListener & Widget & NarratableEntry> T addWidget(T widget) {
        return screen.addRenderableWidget(widget);
    }

    public ComputerContainer getContainer() {
        return screen.getMenu();
    }

    public void addHoverArea(ScreenBase.HoverArea hoverArea) {
        screen.addHoverArea(hoverArea);
    }

    public void playSound(SoundEvent sound) {
        mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1F));
    }

    public void playClickSound() {
        playSound(ModSounds.MOUSE_CLICK);
    }
}
