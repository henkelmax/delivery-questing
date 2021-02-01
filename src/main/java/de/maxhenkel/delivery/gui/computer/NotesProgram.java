package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.gui.TaskWidget;
import de.maxhenkel.delivery.tasks.ActiveTask;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
            return new TaskWidget(guiLeft + xSize / 2 - 106 / 2, guiTop + 3 + 9 + 3 + 10, activeTask, true, t -> {
                screen.setProgram(new ContractProgram(screen, this, t.getTask().getId()));
            }, TASK);
        }).collect(Collectors.toList());

        if (tasks.isEmpty()) {
            return;
        }

        currentTaskWidget = tasks.get(currentTask);

        prev = new Button(guiLeft + 3 + 6, guiTop + ySize - 3 - 6 - 20, 26, 20, new TranslationTextComponent("button.delivery.previous"), button -> {
            currentTask = Math.floorMod(currentTask - 1, tasks.size());
            screen.init();
        });
        next = new Button(guiLeft + xSize - 3 - 6 - 26, guiTop + ySize - 3 - 6 - 20, 26, 20, new TranslationTextComponent("button.delivery.next"), button -> {
            currentTask = Math.floorMod(currentTask + 1, tasks.size());
            screen.init();
        });

        if (tasks.size() == 1) {
            prev.visible = false;
            next.visible = false;
        }

        addWidget(currentTaskWidget);
        addWidget(prev);
        addWidget(next);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bindTexture(BACKGROUND);
        screen.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188);

        if (close.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + close.getPosX(), guiTop + close.getPosY(), 0, 188, close.getWidth(), close.getHeight());
        }

        mc.fontRenderer.func_243248_b(matrixStack, new TranslationTextComponent("message.delivery.notes"), guiLeft + 5, guiTop + 4, 0xFFFFFF);

        if (tasks.isEmpty()) {
            screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.no_tasks"), guiLeft + xSize / 2, guiTop + xSize / 2 - mc.fontRenderer.FONT_HEIGHT, ScreenBase.FONT_COLOR);
            return;
        }

        screen.drawCentered(matrixStack, new StringTextComponent(activeTasks.get(currentTask).getTask().getName()), guiLeft + xSize / 2, guiTop + 3 + 9 + 3, 0);

        screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.task_page", currentTask + 1, tasks.size()), guiLeft + xSize / 2, guiTop + ySize - 3 - 6 - 10, ScreenBase.FONT_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(parent);
            playClickSound();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
