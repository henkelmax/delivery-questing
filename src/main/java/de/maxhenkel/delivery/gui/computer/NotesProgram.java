package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.gui.TaskWidget;
import de.maxhenkel.delivery.tasks.ActiveTask;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class NotesProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/notes.png");
    public static final ResourceLocation TASK = new ResourceLocation(Main.MODID, "textures/gui/container/computer_task.png");

    private ScreenBase.HoverArea close;
    @Nullable
    private TaskWidget currentTaskWidget;
    @Nullable
    private Button prev;
    @Nullable
    private Button next;
    private List<TaskWidget> tasks;
    private List<ActiveTask> activeTasks;
    private int currentTask;

    private ComputerProgram parent;

    public NotesProgram(ComputerScreen screen, ComputerProgram parent) {
        super(screen);
        this.parent = parent;

        activeTasks = getContainer().getGroup().getActiveTasks().getTasks();
    }

    @Override
    protected void init() {
        super.init();

        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);

        tasks = activeTasks.stream().map(activeTask -> {
            return new TaskWidget(xSize / 2 - 106 / 2, 3 + 9 + 30, activeTask, true, t -> {
                screen.setProgram(new ContractProgram(screen, this, t.getTask().getId()));
            }, TASK);
        }).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return;
        }

        currentTaskWidget = tasks.get(currentTask);

        prev = new Button(guiLeft + 3 + 6, guiTop + ySize - 3 - 6 - 20, 26, 20, new TranslatableComponent("button.delivery.previous"), button -> {
            currentTask = Math.floorMod(currentTask - 1, tasks.size());
            screen.init();
        });
        next = new Button(guiLeft + xSize - 3 - 6 - 26, guiTop + ySize - 3 - 6 - 20, 26, 20, new TranslatableComponent("button.delivery.next"), button -> {
            currentTask = Math.floorMod(currentTask + 1, tasks.size());
            screen.init();
        });

        if (tasks.size() == 1) {
            prev.visible = false;
            next.visible = false;
        }

        addWidget(prev);
        addWidget(next);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(PoseStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        drawLevel(matrixStack, new TextComponent(String.valueOf((int) Math.floor(getContainer().getGroup().getLevel()))));

        if (currentTaskWidget != null) {
            currentTaskWidget.render(matrixStack, mouseX - guiLeft, mouseY - guiTop);
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

        screen.blit(matrixStack, guiLeft + 3 + 44, guiTop + 3 + 9 + 10, 0, 197, 162, 5);
        screen.blit(matrixStack, guiLeft + 3 + 44, guiTop + 3 + 9 + 10, 0, 202, (int) (((float) 162) * (getContainer().getGroup().getLevel() - Math.floor(getContainer().getGroup().getLevel()))), 5);

        mc.font.draw(matrixStack, new TranslatableComponent("message.delivery.notes"), guiLeft + 5, guiTop + 4, 0xFFFFFF);

        if (tasks.isEmpty()) {
            screen.drawCentered(matrixStack, new TranslatableComponent("message.delivery.no_tasks"), guiLeft + xSize / 2, guiTop + ySize / 2 - mc.font.lineHeight, ScreenBase.FONT_COLOR);
            return;
        }

        screen.drawCentered(matrixStack, new TextComponent(activeTasks.get(currentTask).getTask().getName()), guiLeft + xSize / 2, guiTop + 3 + 9 + 20, 0);

        screen.drawCentered(matrixStack, new TranslatableComponent("message.delivery.task_page", currentTask + 1, tasks.size()), guiLeft + xSize / 2, guiTop + ySize - 3 - 6 - 10, ScreenBase.FONT_COLOR);
    }

    private void drawLevel(PoseStack matrixStack, MutableComponent text) {
        int w = mc.font.width(text);
        int xPos = xSize / 2 - w / 2;
        mc.font.draw(matrixStack, text, xPos + 1, 17, 0);
        mc.font.draw(matrixStack, text, xPos - 1, 17, 0);
        mc.font.draw(matrixStack, text, xPos, 18, 0);
        mc.font.draw(matrixStack, text, xPos, 16, 0);
        mc.font.draw(matrixStack, text, xPos, 17, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(parent);
            playClickSound();
            return true;
        }

        if (currentTaskWidget != null) {
            if (currentTaskWidget.mouseClicked(mouseX - guiLeft, mouseY - guiTop, button)) {
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (currentTaskWidget != null) {
            if (currentTaskWidget.mouseReleased(mouseX - guiLeft, mouseY - guiTop, button)) {
                return true;
            }
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Nullable
    public TaskWidget getTaskWidget() {
        return currentTaskWidget;
    }
}
