package de.maxhenkel.delivery.gui.computer;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.gui.TaskWidget;
import de.maxhenkel.delivery.net.MessageAcceptTask;
import de.maxhenkel.delivery.tasks.ActiveTask;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;
import java.util.UUID;

public class ContractProgram extends ComputerProgram {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/computer/contract.png");
    public static final ResourceLocation TASK = new ResourceLocation(Main.MODID, "textures/gui/container/computer_task.png");

    private Task task;
    private ComputerProgram parent;
    private DummyPlayer player;
    private TaskWidget taskWidget;
    private ScreenBase.HoverArea close;
    private Button accept;

    public ContractProgram(ComputerScreen screen, ComputerProgram parent, UUID taskID) {
        super(screen);
        this.parent = parent;

        task = Main.TASK_MANAGER.getTask(taskID, getContainer().getGroup());

        if (task == null) {
            screen.setProgram(this.parent);
        }
    }

    @Override
    protected void init() {
        super.init();
        player = new DummyPlayer(mc.level, task.getSkin(), task.getContractorName());
        taskWidget = new TaskWidget(xSize - 106 - 6, 15, new ActiveTask(task, null), false, null, TASK);

        close = new ScreenBase.HoverArea(xSize - 3 - 9, 3, 9, 9);

        accept = new Button(guiLeft + 3 + 54 + 10, guiTop + 3 + 67, 68, 20, new TranslationTextComponent("message.delivery.accept"), button -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageAcceptTask(task.getId()));
            screen.getMenu().getGroup().addTask(task.getId());
            screen.setProgram(parent);
        });
        accept.active = screen.getMenu().getGroup().canAcceptTask(task.getId());
        addWidget(accept);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);

        taskWidget.render(matrixStack, mouseX - guiLeft, mouseY - guiTop);

        if (accept.isHovered() && !accept.active) {
            screen.renderTooltip(matrixStack, new TranslationTextComponent("message.delivery.contract_already_accepted"), mouseX - guiLeft, mouseY - guiTop);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        mc.getTextureManager().bind(BACKGROUND);
        screen.blit(matrixStack, guiLeft + 3, guiTop + 3, 0, 0, 250, 188);

        if (close.isHovered(guiLeft, guiTop, mouseX, mouseY)) {
            screen.blit(matrixStack, guiLeft + close.getPosX(), guiTop + close.getPosY(), 0, 188, close.getWidth(), close.getHeight());
        }

        InventoryScreen.renderEntityInInventory(guiLeft + 31, guiTop + 87, 30, guiLeft + 31 - mouseX, guiTop + 51 - mouseY, player);

        FontRenderer font = mc.font;

        font.draw(matrixStack, new StringTextComponent(task.getName()), guiLeft + 5, guiTop + 4, 0xFFFFFF);

        font.draw(matrixStack, new StringTextComponent(task.getProfession()), guiLeft + 3 + 3, guiTop + 3 + 90, 0);

        screen.drawCentered(matrixStack, new TranslationTextComponent("message.delivery.rewards"), guiLeft + 3 + 54 + 44, guiTop + 3 + 9 + 3, 0);
        font.draw(matrixStack, new TranslationTextComponent("message.delivery.reward_xp", task.getExperience()), guiLeft + 3 + 60, guiTop + 3 + 9 + 3 + 10, screen.FONT_COLOR);
        if (task.getMoney() > 0) {
            font.draw(matrixStack, new TranslationTextComponent("message.delivery.reward_money", task.getMoney()), guiLeft + 3 + 60, guiTop + 3 + 9 + 3 + 20, screen.FONT_COLOR);
        }

        int paddingLeft = guiLeft + 8 + 3;
        int lineHeight = font.lineHeight + 2;
        int yPos = guiTop + 104 + 9 + 3 + 3 + 4;

        screen.drawCentered(matrixStack, new StringTextComponent(task.getName()).withStyle(TextFormatting.BLACK), guiLeft + xSize / 2, yPos, 0);

        yPos += lineHeight + 2;

        List<IReorderingProcessor> list = font.split(new StringTextComponent(task.getDescription()), xSize - 16);
        for (IReorderingProcessor text : list) {
            font.draw(matrixStack, text, paddingLeft, yPos, screen.FONT_COLOR);
            yPos += lineHeight;
        }
    }

    public TaskWidget getTaskWidget() {
        return taskWidget;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (close.isHovered(guiLeft, guiTop, (int) mouseX, (int) mouseY)) {
            screen.setProgram(parent);
            playClickSound();
            return true;
        }

        if (taskWidget.mouseClicked(mouseX - guiLeft, mouseY - guiTop, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        if (taskWidget.mouseReleased(mouseX - guiLeft, mouseY - guiTop, button)) {
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }
}
