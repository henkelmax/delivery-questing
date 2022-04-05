package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.blockentity.SimpleBlockEntityTicker;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.ComputerTileEntity;
import de.maxhenkel.delivery.gui.computer.ComputerContainer;
import de.maxhenkel.delivery.gui.containerprovider.GroupContainerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
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

public class ComputerBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, IGroupBlock {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.box(5D, 0D, 2D, 12D, 1D, 5D),
            Block.box(2D, 0D, 10D, 10D, 1D, 13D),
            Block.box(0D, 4D, 9D, 1D, 12D, 11D),
            Block.box(11D, 4D, 9D, 12D, 12D, 11D),
            Block.box(5D, 0D, 9D, 7D, 4D, 10D),
            Block.box(1D, 11D, 9D, 11D, 12D, 11D),
            Block.box(1D, 4D, 9D, 11D, 5D, 11D),
            Block.box(1D, 5D, 10D, 11D, 11D, 11D),
            Block.box(1D, 0D, 2D, 3D, 1D, 5D),
            Block.box(12D, 0D, 4D, 16D, 10D, 16D)
    ).direction(Direction.SOUTH,
            Block.box(4D, 0D, 11D, 11D, 1D, 14D),
            Block.box(6D, 0D, 3D, 14D, 1D, 6D),
            Block.box(15D, 4D, 5D, 16D, 12D, 7D),
            Block.box(4D, 4D, 5D, 5D, 12D, 7D),
            Block.box(9D, 0D, 6D, 11D, 4D, 7D),
            Block.box(5D, 11D, 5D, 15D, 12D, 7D),
            Block.box(5D, 4D, 5D, 15D, 5D, 7D),
            Block.box(5D, 5D, 5D, 15D, 11D, 6D),
            Block.box(13D, 0D, 11D, 15D, 1D, 14D),
            Block.box(0D, 0D, 0D, 4D, 10D, 12D)
    ).direction(Direction.WEST,
            Block.box(2D, 0D, 4D, 5D, 1D, 11D),
            Block.box(10D, 0D, 6D, 13D, 1D, 14D),
            Block.box(9D, 4D, 15D, 11D, 12D, 16D),
            Block.box(9D, 4D, 4D, 11D, 12D, 5D),
            Block.box(9D, 0D, 9D, 10D, 4D, 11D),
            Block.box(9D, 11D, 5D, 11D, 12D, 15D),
            Block.box(9D, 4D, 5D, 11D, 5D, 15D),
            Block.box(10D, 5D, 5D, 11D, 11D, 15D),
            Block.box(2D, 0D, 13D, 5D, 1D, 15D),
            Block.box(4D, 0D, 0D, 16D, 10D, 4D)
    ).direction(Direction.EAST,
            Block.box(11D, 0D, 5D, 14D, 1D, 12D),
            Block.box(3D, 0D, 2D, 6D, 1D, 10D),
            Block.box(5D, 4D, 0D, 7D, 12D, 1D),
            Block.box(5D, 4D, 11D, 7D, 12D, 12D),
            Block.box(6D, 0D, 5D, 7D, 4D, 7D),
            Block.box(5D, 11D, 1D, 7D, 12D, 11D),
            Block.box(5D, 4D, 1D, 7D, 5D, 11D),
            Block.box(5D, 5D, 1D, 6D, 11D, 11D),
            Block.box(11D, 0D, 1D, 14D, 1D, 3D),
            Block.box(0D, 0D, 12D, 12D, 10D, 16D)
    ).build();

    public static final BooleanProperty ON = BooleanProperty.create("on");

    public ComputerBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.LANTERN).noOcclusion().strength(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "computer"));

        registerDefaultState(defaultBlockState().setValue(ON, false));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player p, InteractionHand handIn, BlockHitResult hit) {
        getGroup(worldIn, pos, p).ifPresent(group -> {
            if (group.getLevel() < Main.SERVER_CONFIG.minComputerLevel.get()) {
                p.displayClientMessage(new TranslatableComponent("message.delivery.computer_level_required", Main.SERVER_CONFIG.minComputerLevel.get()), true);
                return;
            }

            BlockEntity te = worldIn.getBlockEntity(pos);
            if (!(te instanceof ComputerTileEntity)) {
                return;
            }

            ComputerTileEntity computer = (ComputerTileEntity) te;

            if (state.getValue(ON)) {
                group.validateEMails();
                GroupContainerProvider.openGui(p, computer, group, new TranslatableComponent(getDescriptionId()), ComputerContainer::new);
            } else {
                p.displayClientMessage(new TranslatableComponent("message.delivery.computer_no_power"), true);
            }

        });
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
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
        builder.add(ON);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ComputerTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }
}
