package de.maxhenkel.delivery.entity.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.client.RenderUtils;
import de.maxhenkel.corelib.client.obj.OBJEntityRenderer;
import de.maxhenkel.corelib.client.obj.OBJModel;
import de.maxhenkel.corelib.client.obj.OBJModelInstance;
import de.maxhenkel.corelib.client.obj.OBJModelOptions;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.DroneEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
                                matrixStack.rotate(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
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
                                matrixStack.rotate(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
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
                                matrixStack.rotate(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
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
                                matrixStack.rotate(Vector3f.YP.rotationDegrees(-drone.getPropellerRotation(partialTicks)));
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
    private BlockState cachedPayload;

    public DroneRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.mc = Minecraft.getInstance();
    }

    @Override
    public void render(DroneEntity entity, float yaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        super.render(entity, yaw, partialTicks, matrixStack, buffer, packedLight);

        if (!entity.isLoaded()) {
            cachedPayload = null;
            return;
        }

        if (cachedPayload == null) {
            Item item = entity.getPayload().getItem();

            if (!(item instanceof BlockItem)) {
                return;
            }
            BlockItem blockItem = (BlockItem) item;
            cachedPayload = blockItem.getBlock().getDefaultState();
        }
        matrixStack.push();
        matrixStack.translate(-0.5D, -1.5D, -0.5D);
        matrixStack.translate(0.5D / 16D, 0.5D / 16D, 0.5D / 16D);
        matrixStack.scale(15F / 16F, 15F / 16F, 15F / 16F);
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        int color = mc.getBlockColors().getColor(cachedPayload, null, null, 0);
        dispatcher.getBlockModelRenderer().renderModel(matrixStack.getLast(), buffer.getBuffer(RenderTypeLookup.func_239221_b_(cachedPayload)), cachedPayload, dispatcher.getModelForState(cachedPayload), RenderUtils.getRed(color), RenderUtils.getGreen(color), RenderUtils.getBlue(color), packedLight, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        matrixStack.pop();
    }

    @Override
    public List<OBJModelInstance<DroneEntity>> getModels(DroneEntity droneEntity) {
        return MODELS;
    }

}
