package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.integration.jei.ITaskWidgetScreen;
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

public class BulletinBoardScreen extends ScreenBase<BulletinBoardContainer> implements ITaskWidgetScreen {

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
        imageWidth = 176;
        imageHeight = 167;
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

        tasks = menu.getGroup().getActiveTasks().getTasks().stream().map(activeTask -> new TaskWidget(35, 35, activeTask, true, t -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageShowTask(t.getTaskProgress().getTaskID()));
        })).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return;
        }

        currentTaskWidget = tasks.get(currentTask);

        prev = new Button(leftPos + 6, topPos + 140, 26, 20, new TranslationTextComponent("button.delivery.previous"), button -> {
            currentTask = Math.floorMod(currentTask - 1, tasks.size());
            updateScreen();
        });
        next = new Button(leftPos + 144, topPos + 140, 26, 20, new TranslationTextComponent("button.delivery.next"), button -> {
            currentTask = Math.floorMod(currentTask + 1, tasks.size());
            updateScreen();
        });

        if (tasks.size() == 1) {
            prev.visible = false;
            next.visible = false;
        }

        addButton(prev);
        addButton(next);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, new TranslationTextComponent("message.delivery.experience"), 8, FONT_COLOR);

        drawLevel(matrixStack, new StringTextComponent(String.valueOf((int) Math.floor(menu.getGroup().getLevel()))));

        if (!tasks.isEmpty()) {
            drawCentered(matrixStack, new TranslationTextComponent("message.delivery.task_page", currentTask + 1, tasks.size()), 145, FONT_COLOR);
        } else {
            drawCentered(matrixStack, new TranslationTextComponent("message.delivery.no_tasks"), 65, FONT_COLOR);
        }

        if (currentTaskWidget != null) {
            currentTaskWidget.render(matrixStack, x - leftPos, y - topPos);
        }
    }

    private void drawLevel(MatrixStack matrixStack, IFormattableTextComponent text) {
        int w = font.width(text);
        int xPos = imageWidth / 2 - w / 2;
        font.draw(matrixStack, text, xPos + 1, 20, 0);
        font.draw(matrixStack, text, xPos - 1, 20, 0);
        font.draw(matrixStack, text, xPos, 21, 0);
        font.draw(matrixStack, text, xPos, 19, 0);
        font.draw(matrixStack, text, xPos, 20, 0xFFFFFF);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);

        blit(matrixStack, leftPos + 7, topPos + 25, 0, 223, 162, 5);
        blit(matrixStack, leftPos + 7, topPos + 25, 0, 228, (int) (((float) 162) * (menu.getGroup().getLevel() - Math.floor(menu.getGroup().getLevel()))), 5);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (currentTaskWidget != null) {
            if (currentTaskWidget.mouseClicked(mouseX - leftPos, mouseY - topPos, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (currentTaskWidget != null) {
            if (currentTaskWidget.mouseReleased(mouseX - leftPos, mouseY - topPos, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Nullable
    @Override
    public TaskWidget getTaskWidget() {
        return currentTaskWidget;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}