package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Collections;

public class DronePadScreen extends ScreenBase<DronePadContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/drone_pad.png");
    private static final int BAR_HEIGHT = 53;
    private static final int BAR_WIDTH = 16;

    private Inventory playerInventory;

    public DronePadScreen(DronePadContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 166;

        hoverAreas.add(new HoverArea(27, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Collections.singletonList(new TranslatableComponent("tooltip.delivery.energy", container.getDronePadTileEntity().getEnergy().getEnergyStored()).getVisualOrderText())
        ));

        hoverAreas.add(new HoverArea(133, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> {
                    DroneEntity droneCached = container.getDronePadTileEntity().getCachedDroneOnPad();
                    ArrayList<FormattedCharSequence> tooltip = new ArrayList<>();
                    if (droneCached == null) {
                        tooltip.add(new TranslatableComponent("tooltip.delivery.drone_not_on_pad").getVisualOrderText());
                    } else {
                        tooltip.add(new TranslatableComponent("tooltip.delivery.energy", droneCached.getEnergy()).getVisualOrderText());
                    }
                    return tooltip;
                }
        ));
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int x, int y) {
        font.draw(matrixStack, title, 26, 7, FONT_COLOR);
        MutableComponent txt = new TranslatableComponent("entity.delivery.drone");
        font.draw(matrixStack, txt, imageWidth - 26 - font.width(txt), 7, FONT_COLOR);
        font.draw(matrixStack, playerInventory.getDisplayName(), 8, (float) (imageHeight - 96 + 3), FONT_COLOR);

        drawHoverAreas(matrixStack, x, y);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(matrixStack, partialTicks, mouseX, mouseY);
        DronePadTileEntity dronePad = menu.getDronePadTileEntity();

        int energyHeight = getBlitSize(dronePad.getEnergy().getEnergyStored(), dronePad.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
        blit(matrixStack, leftPos + 27, topPos + 17 + energyHeight, 176, energyHeight, BAR_WIDTH, BAR_HEIGHT);

        DroneEntity cachedDroneOnPad = dronePad.getCachedDroneOnPad();
        if (cachedDroneOnPad != null) {
            int droneEnergyHeight = getBlitSize(cachedDroneOnPad.getEnergy(), dronePad.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
            blit(matrixStack, leftPos + 133, topPos + 17 + droneEnergyHeight, 176, droneEnergyHeight, BAR_WIDTH, BAR_HEIGHT);
        }
    }

}