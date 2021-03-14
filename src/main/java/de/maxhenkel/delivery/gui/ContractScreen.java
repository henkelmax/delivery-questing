package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.integration.jei.ITaskWidgetScreen;
import de.maxhenkel.delivery.tasks.ActiveTask;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.List;

public class ContractScreen extends ScreenBase<ContractContainer> implements ITaskWidgetScreen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/contract.png");

    private DummyPlayer player;

    private TaskWidget taskWidget;

    public ContractScreen(ContractContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        imageWidth = 176;
        imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        ActiveTask task = new ActiveTask(menu.getTask(), null);
        taskWidget = new TaskWidget(61, 6, task, false, null);
        player = new DummyPlayer(minecraft.level, menu.getTask().getSkin(), menu.getTask().getContractorName());
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        InventoryScreen.renderEntityInInventory(31, 78, 30, leftPos + 31 - mouseX, topPos + 42 - mouseY, player);

        matrixStack.pushPose();
        matrixStack.scale(0.75F, 0.75F, 1F);

        font.draw(matrixStack, new StringTextComponent(menu.getTask().getProfession()), 11, 113, 0);
        font.draw(matrixStack, new TranslationTextComponent("message.delivery.reward_xp", menu.getTask().getExperience()), 11, 126, FONT_COLOR);
        if (menu.getTask().getMoney() > 0) {
            font.draw(matrixStack, new TranslationTextComponent("message.delivery.reward_money", menu.getTask().getMoney()), 11, 136, FONT_COLOR);
        }
        matrixStack.popPose();

        int paddingLeft = 8;
        int lineHeight = font.lineHeight + 2;
        int yPos = 114;

        drawCentered(matrixStack, new StringTextComponent(menu.getTask().getName()).withStyle(TextFormatting.BLACK), imageWidth / 2, yPos, 0);

        matrixStack.pushPose();
        matrixStack.scale(0.5F, 0.5F, 1F);

        yPos += lineHeight + 2;

        List<IReorderingProcessor> list = font.split(new StringTextComponent(menu.getTask().getDescription()), (imageWidth - 16) * 2);
        for (IReorderingProcessor text : list) {
            font.draw(matrixStack, text, paddingLeft * 2, yPos * 2, FONT_COLOR);
            yPos += lineHeight / 2;
        }
        matrixStack.popPose();

        taskWidget.render(matrixStack, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (taskWidget.mouseClicked(mouseX - leftPos, mouseY - topPos, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (taskWidget.mouseReleased(mouseX - leftPos, mouseY - topPos, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Nullable
    @Override
    public TaskWidget getTaskWidget() {
        return taskWidget;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}