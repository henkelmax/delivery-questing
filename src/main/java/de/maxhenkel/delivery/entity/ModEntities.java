package de.maxhenkel.delivery.entity;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.render.DroneRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ModEntities {

    public static EntityType<DroneEntity> DRONE;

    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        DRONE = CommonRegistry.registerEntity(Main.MODID, "drone", EntityClassification.MISC, DroneEntity.class, builder -> {
            builder
                    .setTrackingRange(128)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .size(12F / 16F, 0.25F)
                    .setCustomClientFactory((spawnEntity, world) -> new DroneEntity(world));
        });
        event.getRegistry().register(DRONE);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        RenderingRegistry.registerEntityRenderingHandler(DRONE, DroneRenderer::new);
    }

}
