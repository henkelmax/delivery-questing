package de.maxhenkel.delivery.blocks;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IUpgradable {

    ActionResultType addUpgrade(@Nullable PlayerEntity player, ItemStack stack, World world, BlockPos pos);

}
