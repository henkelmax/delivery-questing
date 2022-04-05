package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.blockentity.SimpleBlockEntityTicker;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.MailboxTileEntity;
import de.maxhenkel.delivery.gui.MailboxContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class MailboxBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, IGroupBlock {

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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player p, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof MailboxTileEntity)) {
            return super.use(state, worldIn, pos, p, handIn, hit);
        }
        MailboxTileEntity mailbox = (MailboxTileEntity) te;

        getGroup(worldIn, pos, p).ifPresent(group1 -> {
            TileEntityContainerProvider.openGui(p, mailbox, (i, playerInventory, playerEntity) -> new MailboxContainer(i, playerInventory, mailbox));
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        setGroup(worldIn, pos, placer);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(HorizontalRotatableBlock.FACING));
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof MailboxTileEntity) {
                MailboxTileEntity mailbox = (MailboxTileEntity) te;
                Containers.dropContents(worldIn, pos, mailbox.getOutbox());
            }
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NEW_MAIL);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MailboxTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }
}
