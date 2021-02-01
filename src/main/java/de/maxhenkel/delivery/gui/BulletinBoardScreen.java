package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.net.MessageShowTask;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class BulletinBoardScreen extends ScreenBase<BulletinBoardContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/bulletin_board.png");

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
        xSize = 176;
        ySize = 167;
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

        tasks = container.getGroup().getActiveTasks().getTasks().stream().map(activeTask -> new TaskWidget(guiLeft + 35, guiTop + 35, activeTask, true, t -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageShowTask(t.getTaskProgress().getTaskID()));
        })).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return;
        }

        currentTaskWidget = tasks.get(currentTask);

        prev = new Button(guiLeft + 6, guiTop + 140, 26, 20, new TranslationTextComponent("button.delivery.previous"), button -> {
            currentTask = Math.floorMod(currentTask - 1, tasks.size());
            updateScreen();
        });
        next = new Button(guiLeft + 144, guiTop + 140, 26, 20, new TranslationTextComponent("button.delivery.next"), button -> {
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
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.experience"), 8, FONT_COLOR);

        drawLevel(matrixStack, new StringTextComponent(String.valueOf((int) Math.floor(container.getGroup().getLevel()))));

        if (!tasks.isEmpty()) {
            drawCentered(matrixStack, new TranslationTextComponent("message.delivery.task_page", currentTask + 1, tasks.size()), 145, FONT_COLOR);
        } else {
            drawCentered(matrixStack, new TranslationTextComponent("message.delivery.no_tasks"), 65, FONT_COLOR);
        }
    }

    private void drawLevel(MatrixStack matrixStack, IFormattableTextComponent text) {
        int w = font.getStringPropertyWidth(text);
        int xPos = xSize / 2 - w / 2;
        font.func_243248_b(matrixStack, text, xPos + 1, 20, 0);
        font.func_243248_b(matrixStack, text, xPos - 1, 20, 0);
        font.func_243248_b(matrixStack, text, xPos, 21, 0);
        font.func_243248_b(matrixStack, text, xPos, 19, 0);
        font.func_243248_b(matrixStack, text, xPos, 20, 0xFFFFFF);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);

        blit(matrixStack, guiLeft + 7, guiTop + 25, 0, 223, 162, 5);
        blit(matrixStack, guiLeft + 7, guiTop + 25, 0, 228, (int) (((float) 162) * (container.getGroup().getLevel() - Math.floor(container.getGroup().getLevel()))), 5);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}