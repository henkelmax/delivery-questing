package de.maxhenkel.delivery.blocks.fluid;

import de.maxhenkel.delivery.damage.DamageSourceEnergy;
import de.maxhenkel.delivery.fluid.ModFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LiquidEnergyBlock extends FlowingFluidBlock {

    public LiquidEnergyBlock() {
        super(() -> ModFluids.LIQUID_ENERGY, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100F).noDrops());
        setRegistryName(getFluid().getRegistryName());
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        entityIn.attackEntityFrom(DamageSourceEnergy.DAMAGE_ENERGY, 4F);
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }
}
