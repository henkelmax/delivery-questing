package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.EnergyLiquifierTileEntity;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PackagerBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider {

    public PackagerBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).notSolid().hardnessAndResistance(3F));
        setRegistryName(new ResourceLocation(Main.MODID, "packager"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (!(te instanceof PackagerTileEntity)) {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        }

        PackagerTileEntity packager = (PackagerTileEntity) te;

        TileEntityContainerProvider.openGui(player, packager, (i, playerInventory, playerEntity) -> new PackagerContainer(i, playerInventory, packager));

        return ActionResultType.SUCCESS;
    }


    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof PackagerTileEntity) {
            PackagerTileEntity energyLiquifier = (PackagerTileEntity) te;
            InventoryHelper.dropInventoryItems(worldIn, pos, energyLiquifier.getInventory());
            InventoryHelper.dropInventoryItems(worldIn, pos, energyLiquifier.getUpgradeInventory());
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new PackagerTileEntity();
    }
}
