package de.maxhenkel.delivery.fluid;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ModFluids {

    public static LiquidEnergySource LIQUID_ENERGY = new LiquidEnergySource();
    public static LiquidEnergyFlowing LIQUID_ENERGY_FLOWING = new LiquidEnergyFlowing();

    public static void registerFluids(RegistryEvent.Register<Fluid> event) {
        event.getRegistry().registerAll(
                LIQUID_ENERGY,
                LIQUID_ENERGY_FLOWING
        );

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ItemBlockRenderTypes.setRenderLayer(LIQUID_ENERGY, RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(LIQUID_ENERGY_FLOWING, RenderType.translucent());
        }
    }

}
