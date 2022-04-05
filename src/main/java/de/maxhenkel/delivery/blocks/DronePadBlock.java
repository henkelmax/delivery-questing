package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.blockentity.SimpleBlockEntityTicker;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.DronePadTileEntity;
import de.maxhenkel.delivery.gui.DronePadContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class DronePadBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, IGroupBlock, IUpgradable {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.box(0D, 0D, 0D, 16D, 1D, 16D),
            Block.box(1D, 1D, 1D, 15D, 2D, 15D),
            Block.box(1D, 1D, 15D, 15D, 12D, 16D),
            Block.box(4D, 2D, 14D, 12D, 9D, 15D)
    ).direction(Direction.SOUTH,
            Block.box(0D, 0D, 0D, 16D, 1D, 16D),
            Block.box(1D, 1D, 1D, 15D, 2D, 15D),
            Block.box(1D, 1D, 0D, 15D, 12D, 1D),
            Block.box(4D, 2D, 1D, 12D, 9D, 2D)
    ).direction(Direction.WEST,
            Block.box(0D, 0D, 0D, 16D, 1D, 16D),
            Block.box(1D, 1D, 1D, 15D, 2D, 15D),
            Block.box(15D, 1D, 1D, 16D, 12D, 15D),
            Block.box(14D, 2D, 4D, 15D, 9D, 12D)
    ).direction(Direction.EAST,
            Block.box(0D, 0D, 0D, 16D, 1D, 16D),
            Block.box(1D, 1D, 1D, 15D, 2D, 15D),
            Block.box(0D, 1D, 1D, 1D, 12D, 15D),
            Block.box(1D, 2D, 4D, 2D, 9D, 12D)
    ).build();

    public DronePadBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion().strength(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "drone_pad"));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player p, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof DronePadTileEntity)) {
            return super.use(state, worldIn, pos, p, handIn, hit);
        }

        DronePadTileEntity dronePad = (DronePadTileEntity) te;

        getGroup(worldIn, pos, p).ifPresent(value -> {
            if (dronePad.isSkyFree()) {
                TileEntityContainerProvider.openGui(p, dronePad, (i, playerInventory, playerEntity) -> new DronePadContainer(i, playerInventory, dronePad));
            } else {
                p.displayClientMessage(new TranslatableComponent("message.delivery.drone_pad_no_sky_access"), true);
            }
        });
        return InteractionResult.SUCCESS;
    }


    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        setGroup(worldIn, pos, placer);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof DronePadTileEntity) {
            DronePadTileEntity dronePad = (DronePadTileEntity) te;
            Containers.dropContents(worldIn, pos, dronePad.getInventory());
            Containers.dropContents(worldIn, pos, dronePad.getUpgradeInventory());
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(HorizontalRotatableBlock.FACING));
    }

    @Override
    public InteractionResult addUpgrade(@Nullable Player player, ItemStack stack, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof DronePadTileEntity) {
            DronePadTileEntity dronePad = (DronePadTileEntity) te;
            if (dronePad.getUpgradeInventory().getItem(0).isEmpty()) {
                dronePad.getUpgradeInventory().setItem(0, stack.copy().split(1));
                ItemUtils.decrItemStack(stack, player);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DronePadTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }

}
