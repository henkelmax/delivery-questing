package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class BulletinBoardScreen extends ScreenBase<BulletinBoardContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/bulletin_board.png");

    private PlayerInventory playerInventory;
    @Nullable
    private TaskWidget currentTaskWidget;
    @Nullable
    private Button prev;
    @Nullable
    private Button next;
    private List<TaskWidget> tasks;
    private int currentTask;

    public BulletinBoardScreen(BulletinBoardContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 223;
    }

    @Override
    protected void init() {
        super.init();

        updateScreen();
    }

    public void updateScreen() {
        buttons.clear();
        children.clear();
        currentTaskWidget = null;
        prev = null;
        next = null;

        tasks = container.getTasks().getTasks().stream().map(activeTask -> new TaskWidget(guiLeft + 35, guiTop + 5, activeTask, true)).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return;
        }

        currentTaskWidget = tasks.get(currentTask);

        prev = new Button(guiLeft + 6, guiTop + 105, 26, 20, new TranslationTextComponent("button.delivery.previous"), button -> {
            currentTask = Math.floorMod(currentTask - 1, tasks.size());
            updateScreen();
        });
        next = new Button(guiLeft + 144, guiTop + 105, 26, 20, new TranslationTextComponent("button.delivery.next"), button -> {
            currentTask = Math.floorMod(currentTask + 1, tasks.size());
            updateScreen();
        });

        if (tasks.size() == 1) {
            prev.visible = false;
            next.visible = false;
        }

        addButton(currentTaskWidget);
        addButton(prev);
        addButton(next);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        if (!tasks.isEmpty()) {
            drawCentered(matrixStack, new TranslationTextComponent("message.delivery.task_page", currentTask + 1, tasks.size()), 114, FONT_COLOR);
        } else {
            drawCentered(matrixStack, new TranslationTextComponent("message.delivery.no_tasks"), 50, FONT_COLOR);
        }

        font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8F, (float) (ySize - 96 + 3), FONT_COLOR);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}