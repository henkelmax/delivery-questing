package de.maxhenkel.delivery.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import de.maxhenkel.corelib.CachedMap;
import de.maxhenkel.corelib.client.RenderUtils;
import de.maxhenkel.corelib.client.obj.OBJEntityRenderer;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.corelib.client.obj.OBJModelInstance;
import de.maxhenkel.corelib.client.obj.OBJModelOptions;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Arrays;
import java.util.List;

public class DroneRenderer extends OBJEntityRenderer<DroneEntity> {

    private static final List<OBJModelInstance<DroneEntity>> MODELS = Arrays.asList(
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/rotor.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/rotor.png"),
                            new Vector3d(4D / 16D, 1D / 16D, 5D / 16D),
                            (drone, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/rotor.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/rotor.png"),
                            new Vector3d(-4D / 16D, 1D / 16D, 5D / 16D),
                            (drone, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/rotor.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/rotor.png"),
                            new Vector3d(4D / 16D, 1D / 16D, -5D / 16D),
                            (drone, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/rotor.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/rotor.png"),
                            new Vector3d(-4D / 16D, 1D / 16D, -5D / 16D),
                            (drone, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                matrixStack.mulPose(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/drone.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/drone.png"),
                            new Vector3d(0D, 1D / 16D, 0D),
                            (drone, matrixStack, partialTicks) -> {
                                matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                            }
                    )
            ),
            new OBJModelInstance<>(
                    new OBJModel(
                            new ResourceLocation(Main.MODID, "models/entity/rope.obj")
                    ),
                    new OBJModelOptions<>(
                            new ResourceLocation(Main.MODID, "textures/entity/rope.png"),
                            new Vector3d(0D, -8D / 16D, 0D),
                            (drone, matrixStack, partialTicks) -> {
                                if (drone.isLoaded()) {
                                    matrixStack.scale(1F / 16F, 1F / 16F, 1F / 16F);
                                } else {
                                    matrixStack.scale(0.0000001F, 0.0000001F, 0.0000001F);
                                }
                            }
                    )
            )
    );

    private Minecraft mc;
    private CachedMap<DroneEntity, IDroneRenderable> cachedPayload;

    public DroneRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        this.mc = Minecraft.getInstance();
        cachedPayload = new CachedMap<>(10_000);
    }

    @Override
    public void render(DroneEntity entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTicks, matrixStack, buffer, packedLight);

        if (!entity.isLoaded()) {
            cachedPayload.remove(entity);
            return;
        }

        IDroneRenderable renderable = cachedPayload.get(entity, () -> {
            ItemStack payload = entity.getPayload();

            if (payload.getItem() instanceof BlockItem) {
                BlockItem blockItem = (BlockItem) payload.getItem();
                BlockState defaultState = blockItem.getBlock().defaultBlockState();
                return (entity1, yaw1, partialTicks1, matrixStack1, buffer1, packedLight1) -> {
                    matrixStack1.pushPose();
                    matrixStack1.translate(-0.5D, -1.5D, -0.5D);
                    matrixStack1.translate(0.5D / 16D, 0.5D / 16D, 0.5D / 16D);
                    matrixStack1.scale(15F / 16F, 15F / 16F, 15F / 16F);
                    BlockRenderDispatcher dispatcher = mc.getBlockRenderer();
                    int color = mc.getBlockColors().getColor(defaultState, null, null, 0);
                    dispatcher.getModelRenderer().renderModel(matrixStack1.last(), buffer1.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(defaultState)), defaultState, dispatcher.getBlockModel(defaultState), RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), packedLight1, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
                    matrixStack1.popPose();
                };
            } else {
                return (entity1, yaw1, partialTicks1, matrixStack1, buffer1, packedLight1) -> {
                    matrixStack1.pushPose();
                    matrixStack1.translate(0D, -0.5D, 0D);
                    matrixStack1.scale(0.5F, 0.5F, 0.5F);
                    mc.getItemRenderer().renderStatic(payload, ItemTransforms.TransformType.FIXED, packedLight1, OverlayTexture.NO_OVERLAY, matrixStack1, buffer1, 0);
                    matrixStack1.popPose();
                };
            }
        });

        if (renderable != null) {
            renderable.render(entity, yaw, partialTicks, matrixStack, buffer, packedLight);
        }
    }

    @Override
    public List<OBJModelInstance<DroneEntity>> getModels(DroneEntity droneEntity) {
        return MODELS;
    }

    private interface IDroneRenderable {
        void render(DroneEntity entity, float yaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight);
    }

}
