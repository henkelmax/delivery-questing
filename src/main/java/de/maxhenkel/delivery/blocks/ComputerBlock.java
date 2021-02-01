package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.ComputerTileEntity;
import de.maxhenkel.delivery.gui.computer.ComputerContainer;
import de.maxhenkel.delivery.gui.containerprovider.GroupContainerProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
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

public class ComputerBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, IGroupBlock {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.makeCuboidShape(12D, 0D, 5D, 5D, 1D, 2D),
            Block.makeCuboidShape(10D, 0D, 13D, 2D, 1D, 10D),
            Block.makeCuboidShape(1D, 4D, 11D, 0D, 12D, 9D),
            Block.makeCuboidShape(12D, 4D, 11D, 11D, 12D, 9D),
            Block.makeCuboidShape(7D, 0D, 10D, 5D, 4D, 9D),
            Block.makeCuboidShape(11D, 11D, 11D, 1D, 12D, 9D),
            Block.makeCuboidShape(11D, 4D, 11D, 1D, 5D, 9D),
            Block.makeCuboidShape(11D, 5D, 11D, 1D, 11D, 10D),
            Block.makeCuboidShape(3D, 0D, 5D, 1D, 1D, 2D),
            Block.makeCuboidShape(16D, 0D, 16D, 12D, 10D, 4D)
    ).direction(Direction.SOUTH,
            Block.makeCuboidShape(4D, 0D, 11D, 11D, 1D, 14D),
            Block.makeCuboidShape(6D, 0D, 3D, 14D, 1D, 6D),
            Block.makeCuboidShape(15D, 4D, 5D, 16D, 12D, 7D),
            Block.makeCuboidShape(4D, 4D, 5D, 5D, 12D, 7D),
            Block.makeCuboidShape(9D, 0D, 6D, 11D, 4D, 7D),
            Block.makeCuboidShape(5D, 11D, 5D, 15D, 12D, 7D),
            Block.makeCuboidShape(5D, 4D, 5D, 15D, 5D, 7D),
            Block.makeCuboidShape(5D, 5D, 5D, 15D, 11D, 6D),
            Block.makeCuboidShape(13D, 0D, 11D, 15D, 1D, 14D),
            Block.makeCuboidShape(0D, 0D, 0D, 4D, 10D, 12D)
    ).direction(Direction.WEST,
            Block.makeCuboidShape(5D, 0D, 4D, 2D, 1D, 11D),
            Block.makeCuboidShape(13D, 0D, 6D, 10D, 1D, 14D),
            Block.makeCuboidShape(11D, 4D, 15D, 9D, 12D, 16D),
            Block.makeCuboidShape(11D, 4D, 4D, 9D, 12D, 5D),
            Block.makeCuboidShape(10D, 0D, 9D, 9D, 4D, 11D),
            Block.makeCuboidShape(11D, 11D, 5D, 9D, 12D, 15D),
            Block.makeCuboidShape(11D, 4D, 5D, 9D, 5D, 15D),
            Block.makeCuboidShape(11D, 5D, 5D, 10D, 11D, 15D),
            Block.makeCuboidShape(5D, 0D, 13D, 2D, 1D, 15D),
            Block.makeCuboidShape(16D, 0D, 0D, 4D, 10D, 4D)
    ).direction(Direction.EAST,
            Block.makeCuboidShape(11D, 0D, 12D, 14D, 1D, 5D),
            Block.makeCuboidShape(3D, 0D, 10D, 6D, 1D, 2D),
            Block.makeCuboidShape(5D, 4D, 1D, 7D, 12D, 0D),
            Block.makeCuboidShape(5D, 4D, 12D, 7D, 12D, 11D),
            Block.makeCuboidShape(6D, 0D, 7D, 7D, 4D, 5D),
            Block.makeCuboidShape(5D, 11D, 11D, 7D, 12D, 1D),
            Block.makeCuboidShape(5D, 4D, 11D, 7D, 5D, 1D),
            Block.makeCuboidShape(5D, 5D, 11D, 6D, 11D, 1D),
            Block.makeCuboidShape(11D, 0D, 3D, 14D, 1D, 1D),
            Block.makeCuboidShape(0D, 0D, 16D, 12D, 10D, 12D)
    ).build();

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public ComputerBlock() {
        super(Properties.create(Material.IRON).sound(SoundType.LANTERN).notSolid().hardnessAndResistance(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "computer"));

        setDefaultState(getDefaultState().with(ON, false));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity p, Hand handIn, BlockRayTraceResult hit) {
        getGroup(worldIn, pos, p).ifPresent(value -> {
            TileEntity te = worldIn.getTileEntity(pos);
            if (!(te instanceof ComputerTileEntity)) {
                return;
            }

            ComputerTileEntity computer = (ComputerTileEntity) te;

            if (state.get(ON)) {
                GroupContainerProvider.openGui(p, computer, value, new TranslationTextComponent(getTranslationKey()), ComputerContainer::new);
            } else {
                p.sendStatusMessage(new TranslationTextComponent("message.delivery_computer_no_power"), true);
            }

        });
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        setGroup(worldIn, pos, placer);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.get(HorizontalRotatableBlock.FACING));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ON);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new ComputerTileEntity();
    }
}
