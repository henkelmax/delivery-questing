package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.EnergyLiquifierTileEntity;
import de.maxhenkel.delivery.gui.EnergyLiquifierContainer;
import net.minecraft.block.Block;
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
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnergyLiquifierBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, IUpgradable {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.box(0D, 0D, 8D, 16D, 16D, 16D),
            Block.box(5D, 0D, 3D, 11D, 16D, 8D),
            Block.box(5D, 1D, 2D, 11D, 15D, 3D),
            Block.box(6D, 2D, 1D, 10D, 14D, 2D),
            Block.box(7D, 3D, 0D, 9D, 13D, 1D),
            Block.box(9D, 4D, 0D, 11D, 12D, 1D),
            Block.box(5D, 4D, 0D, 7D, 12D, 1D),
            Block.box(11D, 6D, 0D, 12D, 10D, 1D),
            Block.box(4D, 6D, 0D, 5D, 10D, 1D),
            Block.box(12D, 7D, 0D, 13D, 9D, 1D),
            Block.box(3D, 7D, 0D, 4D, 9D, 1D),
            Block.box(10D, 3D, 1D, 12D, 13D, 2D),
            Block.box(4D, 3D, 1D, 6D, 13D, 2D),
            Block.box(12D, 4D, 1D, 13D, 12D, 2D),
            Block.box(3D, 4D, 1D, 4D, 12D, 2D),
            Block.box(13D, 6D, 1D, 14D, 10D, 2D),
            Block.box(2D, 6D, 1D, 3D, 10D, 2D),
            Block.box(11D, 1D, 3D, 13D, 15D, 8D),
            Block.box(11D, 2D, 2D, 13D, 14D, 3D),
            Block.box(3D, 2D, 2D, 5D, 14D, 3D),
            Block.box(13D, 3D, 2D, 14D, 13D, 3D),
            Block.box(2D, 3D, 2D, 3D, 13D, 3D),
            Block.box(14D, 5D, 2D, 15D, 11D, 3D),
            Block.box(1D, 5D, 2D, 2D, 11D, 3D),
            Block.box(3D, 1D, 3D, 5D, 15D, 8D),
            Block.box(13D, 2D, 3D, 14D, 14D, 8D),
            Block.box(2D, 2D, 3D, 3D, 14D, 8D),
            Block.box(14D, 3D, 3D, 15D, 13D, 8D),
            Block.box(1D, 3D, 3D, 2D, 13D, 8D),
            Block.box(15D, 5D, 3D, 16D, 11D, 8D),
            Block.box(0D, 5D, 3D, 1D, 11D, 8D)
    ).direction(Direction.SOUTH,
            Block.box(16D, 0D, 8D, 0D, 16D, 0D),
            Block.box(11D, 0D, 13D, 5D, 16D, 8D),
            Block.box(11D, 1D, 14D, 5D, 15D, 13D),
            Block.box(10D, 2D, 15D, 6D, 14D, 14D),
            Block.box(9D, 3D, 16D, 7D, 13D, 15D),
            Block.box(7D, 4D, 16D, 5D, 12D, 15D),
            Block.box(11D, 4D, 16D, 9D, 12D, 15D),
            Block.box(5D, 6D, 16D, 4D, 10D, 15D),
            Block.box(12D, 6D, 16D, 11D, 10D, 15D),
            Block.box(4D, 7D, 16D, 3D, 9D, 15D),
            Block.box(13D, 7D, 16D, 12D, 9D, 15D),
            Block.box(6D, 3D, 15D, 4D, 13D, 14D),
            Block.box(12D, 3D, 15D, 10D, 13D, 14D),
            Block.box(4D, 4D, 15D, 3D, 12D, 14D),
            Block.box(13D, 4D, 15D, 12D, 12D, 14D),
            Block.box(3D, 6D, 15D, 2D, 10D, 14D),
            Block.box(14D, 6D, 15D, 13D, 10D, 14D),
            Block.box(5D, 1D, 13D, 3D, 15D, 8D),
            Block.box(5D, 2D, 14D, 3D, 14D, 13D),
            Block.box(13D, 2D, 14D, 11D, 14D, 13D),
            Block.box(3D, 3D, 14D, 2D, 13D, 13D),
            Block.box(14D, 3D, 14D, 13D, 13D, 13D),
            Block.box(2D, 5D, 14D, 1D, 11D, 13D),
            Block.box(15D, 5D, 14D, 14D, 11D, 13D),
            Block.box(13D, 1D, 13D, 11D, 15D, 8D),
            Block.box(3D, 2D, 13D, 2D, 14D, 8D),
            Block.box(14D, 2D, 13D, 13D, 14D, 8D),
            Block.box(2D, 3D, 13D, 1D, 13D, 8D),
            Block.box(15D, 3D, 13D, 14D, 13D, 8D),
            Block.box(1D, 5D, 13D, 0D, 11D, 8D),
            Block.box(16D, 5D, 13D, 15D, 11D, 8D)
    ).direction(Direction.WEST,
            Block.box(8D, 0D, 16D, 16D, 16D, 0D),
            Block.box(3D, 0D, 11D, 8D, 16D, 5D),
            Block.box(2D, 1D, 11D, 3D, 15D, 5D),
            Block.box(1D, 2D, 10D, 2D, 14D, 6D),
            Block.box(0D, 3D, 9D, 1D, 13D, 7D),
            Block.box(0D, 4D, 7D, 1D, 12D, 5D),
            Block.box(0D, 4D, 11D, 1D, 12D, 9D),
            Block.box(0D, 6D, 5D, 1D, 10D, 4D),
            Block.box(0D, 6D, 12D, 1D, 10D, 11D),
            Block.box(0D, 7D, 4D, 1D, 9D, 3D),
            Block.box(0D, 7D, 13D, 1D, 9D, 12D),
            Block.box(1D, 3D, 6D, 2D, 13D, 4D),
            Block.box(1D, 3D, 12D, 2D, 13D, 10D),
            Block.box(1D, 4D, 4D, 2D, 12D, 3D),
            Block.box(1D, 4D, 13D, 2D, 12D, 12D),
            Block.box(1D, 6D, 3D, 2D, 10D, 2D),
            Block.box(1D, 6D, 14D, 2D, 10D, 13D),
            Block.box(3D, 1D, 5D, 8D, 15D, 3D),
            Block.box(2D, 2D, 5D, 3D, 14D, 3D),
            Block.box(2D, 2D, 13D, 3D, 14D, 11D),
            Block.box(2D, 3D, 3D, 3D, 13D, 2D),
            Block.box(2D, 3D, 14D, 3D, 13D, 13D),
            Block.box(2D, 5D, 2D, 3D, 11D, 1D),
            Block.box(2D, 5D, 15D, 3D, 11D, 14D),
            Block.box(3D, 1D, 13D, 8D, 15D, 11D),
            Block.box(3D, 2D, 3D, 8D, 14D, 2D),
            Block.box(3D, 2D, 14D, 8D, 14D, 13D),
            Block.box(3D, 3D, 2D, 8D, 13D, 1D),
            Block.box(3D, 3D, 15D, 8D, 13D, 14D),
            Block.box(3D, 5D, 1D, 8D, 11D, 0D),
            Block.box(3D, 5D, 16D, 8D, 11D, 15D)
    ).direction(Direction.EAST,
            Block.box(8D, 0D, 0D, 0D, 16D, 16D),
            Block.box(13D, 0D, 5D, 8D, 16D, 11D),
            Block.box(14D, 1D, 5D, 13D, 15D, 11D),
            Block.box(15D, 2D, 6D, 14D, 14D, 10D),
            Block.box(16D, 3D, 7D, 15D, 13D, 9D),
            Block.box(16D, 4D, 9D, 15D, 12D, 11D),
            Block.box(16D, 4D, 5D, 15D, 12D, 7D),
            Block.box(16D, 6D, 11D, 15D, 10D, 12D),
            Block.box(16D, 6D, 4D, 15D, 10D, 5D),
            Block.box(16D, 7D, 12D, 15D, 9D, 13D),
            Block.box(16D, 7D, 3D, 15D, 9D, 4D),
            Block.box(15D, 3D, 10D, 14D, 13D, 12D),
            Block.box(15D, 3D, 4D, 14D, 13D, 6D),
            Block.box(15D, 4D, 12D, 14D, 12D, 13D),
            Block.box(15D, 4D, 3D, 14D, 12D, 4D),
            Block.box(15D, 6D, 13D, 14D, 10D, 14D),
            Block.box(15D, 6D, 2D, 14D, 10D, 3D),
            Block.box(13D, 1D, 11D, 8D, 15D, 13D),
            Block.box(14D, 2D, 11D, 13D, 14D, 13D),
            Block.box(14D, 2D, 3D, 13D, 14D, 5D),
            Block.box(14D, 3D, 13D, 13D, 13D, 14D),
            Block.box(14D, 3D, 2D, 13D, 13D, 3D),
            Block.box(14D, 5D, 14D, 13D, 11D, 15D),
            Block.box(14D, 5D, 1D, 13D, 11D, 2D),
            Block.box(13D, 1D, 3D, 8D, 15D, 5D),
            Block.box(13D, 2D, 13D, 8D, 14D, 14D),
            Block.box(13D, 2D, 2D, 8D, 14D, 3D),
            Block.box(13D, 3D, 14D, 8D, 13D, 15D),
            Block.box(13D, 3D, 1D, 8D, 13D, 2D),
            Block.box(13D, 5D, 15D, 8D, 11D, 16D),
            Block.box(13D, 5D, 0D, 8D, 11D, 1D)
    ).build();

    public EnergyLiquifierBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion().strength(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "energy_liquifier"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof EnergyLiquifierTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }

        EnergyLiquifierTileEntity energyLiquifier = (EnergyLiquifierTileEntity) te;

        TileEntityContainerProvider.openGui(player, energyLiquifier, (i, playerInventory, playerEntity) -> new EnergyLiquifierContainer(i, playerInventory, energyLiquifier));

        return ActionResultType.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.getValue(HorizontalRotatableBlock.FACING));
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof EnergyLiquifierTileEntity) {
            EnergyLiquifierTileEntity energyLiquifier = (EnergyLiquifierTileEntity) te;
            InventoryHelper.dropContents(worldIn, pos, energyLiquifier.getInventory());
            InventoryHelper.dropContents(worldIn, pos, energyLiquifier.getUpgradeInventory());
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public ActionResultType addUpgrade(@Nullable PlayerEntity player, ItemStack stack, World world, BlockPos pos) {
        TileEntity te = world.getBlockEntity(pos);
        if (te instanceof EnergyLiquifierTileEntity) {
            EnergyLiquifierTileEntity liquifier = (EnergyLiquifierTileEntity) te;
            if (liquifier.getUpgradeInventory().getItem(0).isEmpty()) {
                liquifier.getUpgradeInventory().setItem(0, stack.copy().split(1));
                ItemUtils.decrItemStack(stack, player);
                return ActionResultType.sidedSuccess(world.isClientSide);
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new EnergyLiquifierTileEntity();
    }
}
