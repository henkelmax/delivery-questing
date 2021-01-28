package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.EnergyLiquifierTileEntity;
import de.maxhenkel.delivery.net.MessageSwitchLiquifier;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collections;

public class EnergyLiquifierScreen extends ScreenBase<EnergyLiquifierContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/energy_liquifier.png");
    private static final int BAR_HEIGHT = 53;
    private static final int BAR_WIDTH = 16;

    private PlayerInventory playerInventory;

    public EnergyLiquifierScreen(EnergyLiquifierContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 166;

        hoverAreas.add(new HoverArea(27, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Collections.singletonList(new TranslationTextComponent("tooltip.delivery.energy", container.getEnergyLiquifier().getEnergy().getEnergyStored()).func_241878_f())
        ));

        hoverAreas.add(new HoverArea(133, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Arrays.asList(
                        container.getEnergyLiquifier().getTank().getFluid().getDisplayName().func_241878_f(),
                        new TranslationTextComponent("tooltip.delivery.fluid", container.getEnergyLiquifier().getTank().getFluidAmount()).func_241878_f()
                )
        ));
    }

    @Override
    protected void init() {
        super.init();
        addButton(new Button(guiLeft + 74, guiTop + 55, 28, 20, new TranslationTextComponent("button.delivery.switch"), button -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageSwitchLiquifier());
        }));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 8, FONT_COLOR);
        font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8F, (float) (ySize - 96 + 3), FONT_COLOR);

        drawHoverAreas(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        EnergyLiquifierTileEntity energyLiquifier = container.getEnergyLiquifier();

        if (energyLiquifier.isReversed()) {
            blit(matrixStack, guiLeft + 76, guiTop + 36, 176, 0, 24, 15);
        }

        int energyHeight = getBlitSize(energyLiquifier.getEnergy().getEnergyStored(), energyLiquifier.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
        blit(matrixStack, guiLeft + 27, guiTop + 17 + energyHeight, 200, energyHeight, BAR_WIDTH, BAR_HEIGHT);

        int fluidHeight = getBlitSize(energyLiquifier.getTank().getFluidAmount(), energyLiquifier.getTank().getCapacity(), BAR_HEIGHT);
        blit(matrixStack, guiLeft + 133, guiTop + 17 + fluidHeight, 216, fluidHeight, BAR_WIDTH, BAR_HEIGHT);
    }

}