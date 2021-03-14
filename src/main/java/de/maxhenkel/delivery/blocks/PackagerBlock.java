package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.PackagerTileEntity;
import de.maxhenkel.delivery.gui.PackagerContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PackagerBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, IUpgradable {

    public PackagerBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion().strength(3F));
        setRegistryName(new ResourceLocation(Main.MODID, "packager"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof PackagerTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }

        PackagerTileEntity packager = (PackagerTileEntity) te;

        TileEntityContainerProvider.openGui(player, packager, (i, playerInventory, playerEntity) -> new PackagerContainer(i, playerInventory, packager));

        return ActionResultType.SUCCESS;
    }


    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof PackagerTileEntity) {
            PackagerTileEntity energyLiquifier = (PackagerTileEntity) te;
            InventoryHelper.dropContents(worldIn, pos, energyLiquifier.getInventory());
            InventoryHelper.dropContents(worldIn, pos, energyLiquifier.getUpgradeInventory());
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType addUpgrade(@Nullable PlayerEntity player, ItemStack stack, World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof PackagerTileEntity) {
            PackagerTileEntity packager = (PackagerTileEntity) te;
            if (packager.getUpgradeInventory().getItem(0).isEmpty()) {
                packager.getUpgradeInventory().setItem(0, stack.copy().split(1));
                ItemUtils.decrItemStack(stack, player);
                return ActionResultType.sidedSuccess(world.isClientSide);
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new PackagerTileEntity();
    }
}
