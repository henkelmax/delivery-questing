package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.blockentity.SimpleBlockEntityTicker;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.PackagerTileEntity;
import de.maxhenkel.delivery.gui.PackagerContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class PackagerBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, IUpgradable {

    public PackagerBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion().strength(3F));
        setRegistryName(new ResourceLocation(Main.MODID, "packager"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof PackagerTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }

        PackagerTileEntity packager = (PackagerTileEntity) te;

        TileEntityContainerProvider.openGui(player, packager, (i, playerInventory, playerEntity) -> new PackagerContainer(i, playerInventory, packager));

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PackagerTileEntity) {
            PackagerTileEntity energyLiquifier = (PackagerTileEntity) te;
            Containers.dropContents(worldIn, pos, energyLiquifier.getInventory());
            Containers.dropContents(worldIn, pos, energyLiquifier.getUpgradeInventory());
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public InteractionResult addUpgrade(@Nullable Player player, ItemStack stack, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof PackagerTileEntity) {
            PackagerTileEntity packager = (PackagerTileEntity) te;
            if (packager.getUpgradeInventory().getItem(0).isEmpty()) {
                packager.getUpgradeInventory().setItem(0, stack.copy().split(1));
                ItemUtils.decrItemStack(stack, player);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PackagerTileEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }

}
