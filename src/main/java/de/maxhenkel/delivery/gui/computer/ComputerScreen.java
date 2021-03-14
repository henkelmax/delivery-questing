package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.gui.TaskWidget;
import de.maxhenkel.delivery.integration.jei.ITaskWidgetScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class ComputerScreen extends ScreenBase<ComputerContainer> implements ITaskWidgetScreen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/computer.png");

    private PlayerInventory playerInventory;

    @Nullable
    private ComputerProgram program;

    public ComputerScreen(ComputerContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 256;
        imageHeight = 194;

        program = new DesktopProgram(this);
    }

    @Override
    protected void init() {
        buttons.clear();
        children.clear();
        super.init();
        if (program != null) {
            program.init();
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        if (program != null) {
            program.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderLabels(matrixStack, mouseX, mouseY);
        if (program != null) {
            program.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (program != null) {
            if (program.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (program != null) {
            if (program.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (program != null) {
            if (program.mouseScrolled(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return false;
    }

    public PlayerInventory getPlayerInventory() {
        return playerInventory;
    }

    public void setProgram(ComputerProgram program) {
        this.program = program;
        buttons.clear();
        children.clear();
        hoverAreas.clear();
        program.init();
    }

    public void addWidget(Widget widget) {
        addButton(widget);
    }

    public void addHoverArea(HoverArea hoverArea) {
        hoverAreas.add(hoverArea);
    }

    @Nullable
    @Override
    public TaskWidget getTaskWidget() {
        if (program instanceof NotesProgram) {
            NotesProgram notesProgram = (NotesProgram) program;
            return notesProgram.getTaskWidget();
        } else if (program instanceof ContractProgram) {
            ContractProgram contractProgram = (ContractProgram) program;
            return contractProgram.getTaskWidget();
        }
        return null;
    }

    @Override
    public int getXSize() {
        return super.getXSize();
    }

    @Override
    public int getYSize() {
        return super.getYSize();
    }

    @Override
    public int getGuiLeft() {
        return super.getGuiLeft();
    }

    @Override
    public int getGuiTop() {
        return super.getGuiTop();
    }
}