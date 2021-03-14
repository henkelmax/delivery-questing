package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.MailboxTileEntity;
import de.maxhenkel.delivery.gui.MailboxContainer;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class MailboxBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, IGroupBlock {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder()
            .direction(Direction.NORTH,
                    Block.box(5D, 0D, 0D, 11D, 6D, 16D),
                    Block.box(6D, 6D, 0D, 10D, 7D, 16D),
                    Block.box(7D, 7D, 0D, 9D, 8D, 16D)
            ).direction(Direction.SOUTH,
                    Block.box(5D, 0D, 0D, 11D, 6D, 16D),
                    Block.box(6D, 6D, 0D, 10D, 7D, 16D),
                    Block.box(7D, 7D, 0D, 9D, 8D, 16D)
            ).direction(Direction.EAST,
                    Block.box(0D, 0D, 5D, 16D, 6D, 11D),
                    Block.box(0D, 6D, 6D, 16D, 7D, 10D),
                    Block.box(0D, 7D, 7D, 16D, 8D, 9D)
            ).direction(Direction.WEST,
                    Block.box(0D, 0D, 5D, 16D, 6D, 11D),
                    Block.box(0D, 6D, 6D, 16D, 7D, 10D),
                    Block.box(0D, 7D, 7D, 16D, 8D, 9D)
            ).build();

    public static final BooleanProperty NEW_MAIL = BooleanProperty.create("new_mail");

    public MailboxBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion().strength(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "mailbox"));
        registerDefaultState(defaultBlockState().setValue(NEW_MAIL, false));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity p, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof MailboxTileEntity)) {
            return super.use(state, worldIn, pos, p, handIn, hit);
        }
        MailboxTileEntity mailbox = (MailboxTileEntity) te;

        getGroup(worldIn, pos, p).ifPresent(group1 -> {
            TileEntityContainerProvider.openGui(p, mailbox, (i, playerInventory, playerEntity) -> new MailboxContainer(i, playerInventory, mailbox));
        });
        return ActionResultType.SUCCESS;
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        setGroup(worldIn, pos, placer);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE.get(state.getValue(HorizontalRotatableBlock.FACING));
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof MailboxTileEntity) {
                MailboxTileEntity mailbox = (MailboxTileEntity) te;
                InventoryHelper.dropContents(worldIn, pos, mailbox.getOutbox());
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NEW_MAIL);
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new MailboxTileEntity();
    }
}
