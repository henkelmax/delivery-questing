package de.maxhenkel.delivery.blocks.tileentity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import de.maxhenkel.delivery.blocks.BulletinBoardBlock;
import de.maxhenkel.delivery.blocks.HorizontalRotatableBlock;
import de.maxhenkel.delivery.blocks.tileentity.BulletinBoardTileEntity;
import de.maxhenkel.delivery.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class BulletinBoardRenderer implements BlockEntityRenderer<BulletinBoardTileEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack contract;

    public BulletinBoardRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        contract = new ItemStack(ModItems.CONTRACT);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(BulletinBoardTileEntity bulletinBoard, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        int contracts = bulletinBoard.getBlockState().getValue(BulletinBoardBlock.CONTRACTS);
        if (contracts <= 0) {
            return;
        }

        Direction direction = bulletinBoard.getBlockState().getValue(HorizontalRotatableBlock.FACING);

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.mulPose(direction.getRotation());
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(0D, 0D, -0.5D + 15D / 16D);
        matrixStack.scale(0.4F, 0.4F, 0.4F);

        matrixStack.pushPose();
        matrixStack.translate(0.5D, 0.5D, 0D);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(5F));
        itemRenderer.renderStatic(contract, ItemTransforms.TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 0);
        matrixStack.popPose();

        if (contracts >= 2) {
            matrixStack.pushPose();
            matrixStack.translate(-0.5D, 0.5D, 0D);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-2F));
            itemRenderer.renderStatic(contract, ItemTransforms.TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 0);
            matrixStack.popPose();
        }
        if (contracts >= 3) {
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(-4F));
            matrixStack.translate(0D, -0.5D, 0D);
            itemRenderer.renderStatic(contract, ItemTransforms.TransformType.FIXED, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 0);
            matrixStack.popPose();
        }

        matrixStack.popPose();
    }

}
