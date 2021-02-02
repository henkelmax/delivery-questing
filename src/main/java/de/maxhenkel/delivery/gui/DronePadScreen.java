package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;

public class DronePadScreen extends ScreenBase<DronePadContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/drone_pad.png");
    private static final int BAR_HEIGHT = 53;
    private static final int BAR_WIDTH = 16;

    private PlayerInventory playerInventory;

    public DronePadScreen(DronePadContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        xSize = 176;
        ySize = 166;

        hoverAreas.add(new HoverArea(27, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Collections.singletonList(new TranslationTextComponent("tooltip.delivery.energy", container.getDronePadTileEntity().getEnergy().getEnergyStored()).func_241878_f())
        ));

        hoverAreas.add(new HoverArea(133, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> {
                    DroneEntity droneCached = container.getDronePadTileEntity().getCachedDroneOnPad();
                    ArrayList<IReorderingProcessor> tooltip = new ArrayList<>();
                    if (droneCached == null) {
                        tooltip.add(new TranslationTextComponent("tooltip.delivery.drone_not_on_pad").func_241878_f());
                    } else {
                        tooltip.add(new TranslationTextComponent("tooltip.delivery.energy", droneCached.getEnergy()).func_241878_f());
                    }
                    return tooltip;
                }
        ));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        font.func_243248_b(matrixStack, title, 26, 7, FONT_COLOR);
        IFormattableTextComponent txt = new TranslationTextComponent("entity.delivery.drone");
        font.func_243248_b(matrixStack, txt, xSize - 26 - font.getStringPropertyWidth(txt), 7, FONT_COLOR);
        font.func_243248_b(matrixStack, playerInventory.getDisplayName(), 8, (float) (ySize - 96 + 3), FONT_COLOR);

        drawHoverAreas(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
        DronePadTileEntity dronePad = container.getDronePadTileEntity();

        int energyHeight = getBlitSize(dronePad.getEnergy().getEnergyStored(), dronePad.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
        blit(matrixStack, guiLeft + 27, guiTop + 17 + energyHeight, 176, energyHeight, BAR_WIDTH, BAR_HEIGHT);

        DroneEntity cachedDroneOnPad = dronePad.getCachedDroneOnPad();
        if (cachedDroneOnPad != null) {
            int droneEnergyHeight = getBlitSize(cachedDroneOnPad.getEnergy(), dronePad.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
            blit(matrixStack, guiLeft + 133, guiTop + 17 + droneEnergyHeight, 176, droneEnergyHeight, BAR_WIDTH, BAR_HEIGHT);
        }
    }

}