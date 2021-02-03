package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.gui.DronePadContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DronePadBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, IGroupBlock {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.makeCuboidShape(0D, 0D, 0D, 16D, 1D, 16D),
            Block.makeCuboidShape(1D, 1D, 1D, 15D, 2D, 15D),
            Block.makeCuboidShape(1D, 1D, 15D, 15D, 12D, 16D),
            Block.makeCuboidShape(4D, 2D, 14D, 12D, 9D, 15D)
    ).direction(Direction.SOUTH,
            Block.makeCuboidShape(16D, 0D, 16D, 0D, 1D, 0D),
            Block.makeCuboidShape(15D, 1D, 15D, 1D, 2D, 1D),
            Block.makeCuboidShape(15D, 1D, 1D, 1D, 12D, 0D),
            Block.makeCuboidShape(12D, 2D, 2D, 4D, 9D, 1D)
    ).direction(Direction.WEST,
            Block.makeCuboidShape(0D, 0D, 16D, 16D, 1D, 0D),
            Block.makeCuboidShape(1D, 1D, 15D, 15D, 2D, 1D),
            Block.makeCuboidShape(15D, 1D, 15D, 16D, 12D, 1D),
            Block.makeCuboidShape(14D, 2D, 12D, 15D, 9D, 4D)
    ).direction(Direction.EAST,
            Block.makeCuboidShape(16D, 0D, 0D, 0D, 1D, 16D),
            Block.makeCuboidShape(15D, 1D, 1D, 1D, 2D, 15D),
            Block.makeCuboidShape(1D, 1D, 1D, 0D, 12D, 15D),
            Block.makeCuboidShape(2D, 2D, 4D, 1D, 9D, 12D)
    ).build();

    public DronePadBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).notSolid().hardnessAndResistance(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "drone_pad"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity p, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (!(te instanceof DronePadTileEntity)) {
            return super.onBlockActivated(state, worldIn, pos, p, handIn, hit);
        }

        DronePadTileEntity dronePad = (DronePadTileEntity) te;

        getGroup(worldIn, pos, p).ifPresent(value -> {
            if (dronePad.isSkyFree()) {
                TileEntityContainerProvider.openGui(p, dronePad, (i, playerInventory, playerEntity) -> new DronePadContainer(i, playerInventory, dronePad));
            } else {
                p.sendStatusMessage(new TranslationTextComponent("message.delivery.drone_pad_no_sky_access"), true);
            }
        });
        return ActionResultType.SUCCESS;
    }


    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        setGroup(worldIn, pos, placer);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof DronePadTileEntity) {
            DronePadTileEntity dronePad = (DronePadTileEntity) te;
            InventoryHelper.dropInventoryItems(worldIn, pos, dronePad.getInventory());
            InventoryHelper.dropInventoryItems(worldIn, pos, dronePad.getUpgradeInventory());
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.get(HorizontalRotatableBlock.FACING));
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new DronePadTileEntity();
    }
}
