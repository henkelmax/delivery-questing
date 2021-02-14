package de.maxhenkel.delivery.blocks.tileentity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.delivery.blocks.BulletinBoardBlock;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.blocks.tileentity.BulletinBoardTileEntity;
import de.maxhenkel.delivery.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class BulletinBoardRenderer extends TileEntityRenderer<BulletinBoardTileEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack contract;

    public BulletinBoardRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        contract = new ItemStack(ModItems.CONTRACT);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(BulletinBoardTileEntity bulletinBoard, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        int contracts = bulletinBoard.getBlockState().get(BulletinBoardBlock.CONTRACTS);
        if (contracts <= 0) {
            return;
        }

        Direction direction = bulletinBoard.getBlockState().get(HorizontalRotatableBlock.FACING);

        matrixStack.push();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.rotate(direction.getRotation());
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(0D, 0D, -0.5D + 15D / 16D);
        matrixStack.scale(0.4F, 0.4F, 0.4F);

        matrixStack.push();
        matrixStack.translate(0.5D, 0.5D, 0D);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(5F));
        itemRenderer.renderItem(contract, ItemCameraTransforms.TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
        matrixStack.pop();

        if (contracts >= 2) {
            matrixStack.push();
            matrixStack.translate(-0.5D, 0.5D, 0D);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(-2F));
            itemRenderer.renderItem(contract, ItemCameraTransforms.TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            matrixStack.pop();
        }
        if (contracts >= 3) {
            matrixStack.push();
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(-4F));
            matrixStack.translate(0D, -0.5D, 0D);
            itemRenderer.renderItem(contract, ItemCameraTransforms.TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            matrixStack.pop();
        }

        matrixStack.pop();
    }

}
