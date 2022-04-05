package de.maxhenkel.delivery.entity;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.entity.render.DroneRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class ModEntities {

    public static EntityType<DroneEntity> DRONE;

    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        DRONE = CommonRegistry.registerEntity(Main.MODID, "drone", MobCategory.MISC, DroneEntity.class, builder -> {
            builder
                    .setTrackingRange(256)
                    .setUpdateInterval(1)
                    .setShouldReceiveVelocityUpdates(true)
                    .sized(12F / 16F, 0.25F)
                    .setCustomClientFactory((spawnEntity, world) -> new DroneEntity(world));
        });
        event.getRegistry().register(DRONE);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        EntityRenderers.register(DRONE, DroneRenderer::new);
    }

}
