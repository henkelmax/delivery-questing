package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DummyPlayer;
import de.maxhenkel.delivery.tasks.ActiveTask;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;

public class ContractScreen extends ScreenBase<ContractContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/contract.png");

    private DummyPlayer player;

    private PlayerInventory playerInventory;
    private TaskWidget taskWidget;

    public ContractScreen(ContractContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 222;
    }

    @Override
    protected void init() {
        super.init();
        ActiveTask task = new ActiveTask(container.getTask(), null);
        taskWidget = new TaskWidget(guiLeft + 61, guiTop + 6, task, false);
        player = new DummyPlayer(minecraft.world, container.getTask().getSkin(), container.getTask().getContractorName());

        addButton(taskWidget);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        InventoryScreen.drawEntityOnScreen(31, 78, 30, guiLeft + 31 - mouseX, guiTop + 42 - mouseY, player);

        matrixStack.push();
        matrixStack.scale(0.75F, 0.75F, 1F);

        IFormattableTextComponent profession = new StringTextComponent(container.getTask().getProfession()).mergeStyle(TextFormatting.BLACK);
        float width = font.getStringWidth(profession.getString()) * 0.75F;

        font.func_243248_b(matrixStack, profession, 40F - width / 2F, 113, 0);
        font.func_243248_b(matrixStack, new TranslationTextComponent("message.delivery.reward_xp", container.getTask().getExperience()), 11, 128, FONT_COLOR);

        matrixStack.pop();

        String description = container.getTask().getDescription();
        String[] split = description.split("\\s");

        StringBuilder str = new StringBuilder();
        int maxWidth = xSize - 16;
        int paddingLeft = 8;
        int lineHeight = font.FONT_HEIGHT + 2;
        int index = 0;
        int yPos = 114;

        drawCentered(matrixStack, new StringTextComponent(container.getTask().getName()).mergeStyle(TextFormatting.BLACK), xSize / 2, yPos, 0);

        matrixStack.push();
        matrixStack.scale(0.5F, 0.5F, 1F);

        yPos += lineHeight + 2;

        while (index < split.length) {
            if (str.length() > 0 && font.getStringWidth(str.toString() + split[index]) > maxWidth * 2) {
                font.func_243248_b(matrixStack, new StringTextComponent(str.toString()), paddingLeft * 2, yPos * 2, FONT_COLOR);
                yPos += lineHeight / 2;
                str = new StringBuilder(split[index]);
                str.append(" ");
            } else {
                str.append(split[index]);
                str.append(" ");
            }
            index++;
        }
        font.func_243248_b(matrixStack, new StringTextComponent(str.toString()), paddingLeft * 2, yPos * 2, FONT_COLOR);

        matrixStack.pop();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

}