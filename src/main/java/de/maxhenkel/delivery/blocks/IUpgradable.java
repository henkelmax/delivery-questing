package de.maxhenkel.delivery.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface IUpgradable {

    InteractionResult addUpgrade(@Nullable Player player, ItemStack stack, Level world, BlockPos pos);

}
