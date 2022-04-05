package de.maxhenkel.delivery.blocks.fluid;

import de.maxhenkel.delivery.damage.DamageSourceEnergy;
import de.maxhenkel.delivery.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class LiquidEnergyBlock extends LiquidBlock {

    public LiquidEnergyBlock() {
        super(() -> ModFluids.LIQUID_ENERGY, Block.Properties.of(Material.WATER).noCollission().strength(100F).noDrops());
        setRegistryName(getFluid().getRegistryName());
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        entityIn.hurt(DamageSourceEnergy.DAMAGE_ENERGY, 4F);
        super.entityInside(state, worldIn, pos, entityIn);
    }
}
