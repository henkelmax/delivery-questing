package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.EnergyLiquifierTileEntity;
import de.maxhenkel.delivery.net.MessageSwitchLiquifier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;
import java.util.Collections;

public class EnergyLiquifierScreen extends ScreenBase<EnergyLiquifierContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/energy_liquifier.png");
    private static final int BAR_HEIGHT = 53;
    private static final int BAR_WIDTH = 16;

    private Inventory playerInventory;

    public EnergyLiquifierScreen(EnergyLiquifierContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 166;

        hoverAreas.add(new HoverArea(27, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Collections.singletonList(new TranslatableComponent("tooltip.delivery.energy", container.getEnergyLiquifier().getEnergy().getEnergyStored()).getVisualOrderText())
        ));

        hoverAreas.add(new HoverArea(133, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Arrays.asList(
                        container.getEnergyLiquifier().getTank().getFluid().getDisplayName().getVisualOrderText(),
                        new TranslatableComponent("tooltip.delivery.fluid", container.getEnergyLiquifier().getTank().getFluidAmount()).getVisualOrderText()
                )
        ));
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new Button(leftPos + 74, topPos + 55, 28, 20, new TranslatableComponent("button.delivery.switch"), button -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageSwitchLiquifier());
        }));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        drawCentered(matrixStack, title, 6, FONT_COLOR);
        font.draw(matrixStack, playerInventory.getDisplayName(), 8F, (float) (imageHeight - 96 + 3), FONT_COLOR);

        drawHoverAreas(matrixStack, x, y);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        EnergyLiquifierTileEntity energyLiquifier = menu.getEnergyLiquifier();

        if (energyLiquifier.isReversed()) {
            blit(matrixStack, leftPos + 76, topPos + 36, 176, 0, 24, 15);
        }

        int energyHeight = getBlitSize(energyLiquifier.getEnergy().getEnergyStored(), energyLiquifier.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
        blit(matrixStack, leftPos + 27, topPos + 17 + energyHeight, 200, energyHeight, BAR_WIDTH, BAR_HEIGHT);

        int fluidHeight = getBlitSize(energyLiquifier.getTank().getFluidAmount(), energyLiquifier.getTank().getCapacity(), BAR_HEIGHT);
        blit(matrixStack, leftPos + 133, topPos + 17 + fluidHeight, 216, fluidHeight, BAR_WIDTH, BAR_HEIGHT);
    }

}