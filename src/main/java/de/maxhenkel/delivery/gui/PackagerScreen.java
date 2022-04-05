package de.maxhenkel.delivery.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import de.maxhenkel.corelib.client.RenderUtils;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.PackagerTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.Collections;

public class PackagerScreen extends ScreenBase<PackagerContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/packager.png");
    private static final int BAR_HEIGHT = 53;
    private static final int BAR_WIDTH = 16;

    private Inventory playerInventory;

    public PackagerScreen(PackagerContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        this.playerInventory = playerInventory;
        imageWidth = 176;
        imageHeight = 166;

        hoverAreas.add(new HoverArea(30, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> Collections.singletonList(new TranslatableComponent("tooltip.delivery.energy", container.getPackager().getEnergy().getEnergyStored()).getVisualOrderText())
        ));

        hoverAreas.add(new HoverArea(53, 17, BAR_WIDTH, BAR_HEIGHT,
                () -> {
                    if (container.getPackager().getTank().getFluid().isEmpty()) {
                        return Collections.singletonList(new TranslatableComponent("tooltip.delivery.empty").getVisualOrderText());
                    }
                    return Arrays.asList(
                            container.getPackager().getTank().getFluid().getDisplayName().getVisualOrderText(),
                            new TranslatableComponent("tooltip.delivery.fluid", container.getPackager().getTank().getFluidAmount()).getVisualOrderText()
                    );
                }
        ));
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
        PackagerTileEntity packager = menu.getPackager();

        int energyHeight = getBlitSize(packager.getEnergy().getEnergyStored(), packager.getEnergy().getMaxEnergyStored(), BAR_HEIGHT);
        blit(matrixStack, leftPos + 30, topPos + 17 + energyHeight, 176, energyHeight, BAR_WIDTH, BAR_HEIGHT);
        Fluid fluid = packager.getTank().getFluid().getFluid();
        TextureAtlasSprite texture = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(fluid.getAttributes().getStillTexture());

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, texture.atlas().location());

        int fluidHeight = getBlitSize(packager.getTank().getFluidAmount(), packager.getTank().getCapacity(), BAR_HEIGHT);
        int size = BAR_HEIGHT - fluidHeight;
        int i = 0;
        while (size > 0) {
            int s = Math.min(size, 16);
            fluidBlit(matrixStack, leftPos + 53, topPos + 17 + fluidHeight + i * 16, 16, s, texture, fluid.getAttributes().getColor());
            size -= s;
            i++;
        }
    }

    public static void fluidBlit(PoseStack matrixStack, int x, int y, int width, int height, TextureAtlasSprite sprite, int color) {
        innerBlit(matrixStack.last().pose(), x, x + width, y, y + height, sprite.getU0(), sprite.getU1(), sprite.getV0(), (sprite.getV1() - sprite.getV0()) * (float) height / 16F + sprite.getV0(), color);
    }

    private static void innerBlit(Matrix4f matrix, int x1, int x2, int y1, int y2, float minU, float maxU, float minV, float maxV, int color) {
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix, (float) x1, (float) y2, 0F).uv(minU, maxV).color(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255).endVertex();
        bufferbuilder.vertex(matrix, (float) x2, (float) y2, 0F).uv(maxU, maxV).color(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255).endVertex();
        bufferbuilder.vertex(matrix, (float) x2, (float) y1, 0F).uv(maxU, minV).color(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255).endVertex();
        bufferbuilder.vertex(matrix, (float) x1, (float) y1, 0F).uv(minU, minV).color(RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), 255).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
    }

}