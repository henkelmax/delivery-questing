package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.blockentity.SimpleBlockEntityTicker;
import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.BulletinBoardTileEntity;
import de.maxhenkel.delivery.gui.BulletinBoardContainer;
import de.maxhenkel.delivery.gui.containerprovider.GroupContainerProvider;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BulletinBoardBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, IGroupBlock {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.box(0D, 0D, 15D, 16D, 16D, 16D),
            Block.box(0D, 0D, 14D, 1D, 16D, 15D),
            Block.box(15D, 0D, 14D, 16D, 16D, 15D),
            Block.box(1D, 15D, 14D, 15D, 16D, 15D),
            Block.box(1D, 0D, 14D, 15D, 1D, 15D)
    ).direction(Direction.SOUTH,
            Block.box(0D, 0D, 0D, 16D, 16D, 1D),
            Block.box(15D, 0D, 1D, 16D, 16D, 2D),
            Block.box(0D, 0D, 1D, 1D, 16D, 2D),
            Block.box(1D, 15D, 1D, 15D, 16D, 2D),
            Block.box(1D, 0D, 1D, 15D, 1D, 2D)
    ).direction(Direction.WEST,
            Block.box(15D, 0D, 0D, 16D, 16D, 16D),
            Block.box(14D, 0D, 15D, 15D, 16D, 16D),
            Block.box(14D, 0D, 0D, 15D, 16D, 1D),
            Block.box(14D, 15D, 1D, 15D, 16D, 15D),
            Block.box(14D, 0D, 1D, 15D, 1D, 15D)
    ).direction(Direction.EAST,
            Block.box(0D, 0D, 0D, 1D, 16D, 16D),
            Block.box(1D, 0D, 0D, 2D, 16D, 1D),
            Block.box(1D, 0D, 15D, 2D, 16D, 16D),
            Block.box(1D, 15D, 1D, 2D, 16D, 15D),
            Block.box(1D, 0D, 1D, 2D, 1D, 15D)
    ).build();

    public static final IntegerProperty CONTRACTS = IntegerProperty.create("contracts", 0, 3);

    public BulletinBoardBlock() {
        super(Properties.of(Material.WOOD).sound(SoundType.WOOD).noOcclusion().strength(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "bulletin_board"));
        registerDefaultState(defaultBlockState().setValue(CONTRACTS, 0));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player p, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof BulletinBoardTileEntity)) {
            return super.use(state, worldIn, pos, p, handIn, hit);
        }
        BulletinBoardTileEntity tileEntity = (BulletinBoardTileEntity) te;

        if (!(p instanceof ServerPlayer)) {
            return InteractionResult.SUCCESS;
        }
        ServerPlayer player = (ServerPlayer) p;

        Optional<Group> group = getGroup(worldIn, pos, p);
        if (group.isPresent()) {
            ItemStack heldItem = player.getItemInHand(handIn);
            if (heldItem.getItem() == ModItems.CONTRACT) {
                UUID task = ModItems.CONTRACT.getTask(heldItem);
                if (task != null) {
                    heldItem.setCount(heldItem.getCount() - 1);
                    if (group.get().canAcceptTask(task)) {
                        group.get().addTask(task);
                        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.level));
                    } else {
                        player.displayClientMessage(new TranslatableComponent("message.delivery.contract_already_accepted"), true);
                    }
                    return InteractionResult.SUCCESS;
                }
            }

            GroupContainerProvider.openGui(player, tileEntity, group.get(), new TranslatableComponent(getDescriptionId()), BulletinBoardContainer::new);
        }
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONTRACTS);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BulletinBoardTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }

}
