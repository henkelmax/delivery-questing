package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.blockentity.SimpleBlockEntityTicker;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.EnergyLiquifierTileEntity;
import de.maxhenkel.delivery.gui.EnergyLiquifierContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class EnergyLiquifierBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, IUpgradable {

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
            Block.box(0D, 0D, 0D, 16D, 16D, 8D),
            Block.box(5D, 0D, 8D, 11D, 16D, 13D),
            Block.box(5D, 1D, 13D, 11D, 15D, 14D),
            Block.box(6D, 2D, 14D, 10D, 14D, 15D),
            Block.box(7D, 3D, 15D, 9D, 13D, 16D),
            Block.box(5D, 4D, 15D, 7D, 12D, 16D),
            Block.box(9D, 4D, 15D, 11D, 12D, 16D),
            Block.box(4D, 6D, 15D, 5D, 10D, 16D),
            Block.box(11D, 6D, 15D, 12D, 10D, 16D),
            Block.box(3D, 7D, 15D, 4D, 9D, 16D),
            Block.box(12D, 7D, 15D, 13D, 9D, 16D),
            Block.box(4D, 3D, 14D, 6D, 13D, 15D),
            Block.box(10D, 3D, 14D, 12D, 13D, 15D),
            Block.box(3D, 4D, 14D, 4D, 12D, 15D),
            Block.box(12D, 4D, 14D, 13D, 12D, 15D),
            Block.box(2D, 6D, 14D, 3D, 10D, 15D),
            Block.box(13D, 6D, 14D, 14D, 10D, 15D),
            Block.box(3D, 1D, 8D, 5D, 15D, 13D),
            Block.box(3D, 2D, 13D, 5D, 14D, 14D),
            Block.box(11D, 2D, 13D, 13D, 14D, 14D),
            Block.box(2D, 3D, 13D, 3D, 13D, 14D),
            Block.box(13D, 3D, 13D, 14D, 13D, 14D),
            Block.box(1D, 5D, 13D, 2D, 11D, 14D),
            Block.box(14D, 5D, 13D, 15D, 11D, 14D),
            Block.box(11D, 1D, 8D, 13D, 15D, 13D),
            Block.box(2D, 2D, 8D, 3D, 14D, 13D),
            Block.box(13D, 2D, 8D, 14D, 14D, 13D),
            Block.box(1D, 3D, 8D, 2D, 13D, 13D),
            Block.box(14D, 3D, 8D, 15D, 13D, 13D),
            Block.box(0D, 5D, 8D, 1D, 11D, 13D),
            Block.box(15D, 5D, 8D, 16D, 11D, 13D)
    ).direction(Direction.WEST,
            Block.box(8D, 0D, 0D, 16D, 16D, 16D),
            Block.box(3D, 0D, 5D, 8D, 16D, 11D),
            Block.box(2D, 1D, 5D, 3D, 15D, 11D),
            Block.box(1D, 2D, 6D, 2D, 14D, 10D),
            Block.box(0D, 3D, 7D, 1D, 13D, 9D),
            Block.box(0D, 4D, 5D, 1D, 12D, 7D),
            Block.box(0D, 4D, 9D, 1D, 12D, 11D),
            Block.box(0D, 6D, 4D, 1D, 10D, 5D),
            Block.box(0D, 6D, 11D, 1D, 10D, 12D),
            Block.box(0D, 7D, 3D, 1D, 9D, 4D),
            Block.box(0D, 7D, 12D, 1D, 9D, 13D),
            Block.box(1D, 3D, 4D, 2D, 13D, 6D),
            Block.box(1D, 3D, 10D, 2D, 13D, 12D),
            Block.box(1D, 4D, 3D, 2D, 12D, 4D),
            Block.box(1D, 4D, 12D, 2D, 12D, 13D),
            Block.box(1D, 6D, 2D, 2D, 10D, 3D),
            Block.box(1D, 6D, 13D, 2D, 10D, 14D),
            Block.box(3D, 1D, 3D, 8D, 15D, 5D),
            Block.box(2D, 2D, 3D, 3D, 14D, 5D),
            Block.box(2D, 2D, 11D, 3D, 14D, 13D),
            Block.box(2D, 3D, 2D, 3D, 13D, 3D),
            Block.box(2D, 3D, 13D, 3D, 13D, 14D),
            Block.box(2D, 5D, 1D, 3D, 11D, 2D),
            Block.box(2D, 5D, 14D, 3D, 11D, 15D),
            Block.box(3D, 1D, 11D, 8D, 15D, 13D),
            Block.box(3D, 2D, 2D, 8D, 14D, 3D),
            Block.box(3D, 2D, 13D, 8D, 14D, 14D),
            Block.box(3D, 3D, 1D, 8D, 13D, 2D),
            Block.box(3D, 3D, 14D, 8D, 13D, 15D),
            Block.box(3D, 5D, 0D, 8D, 11D, 1D),
            Block.box(3D, 5D, 15D, 8D, 11D, 16D)
    ).direction(Direction.EAST,
            Block.box(0D, 0D, 0D, 8D, 16D, 16D),
            Block.box(8D, 0D, 5D, 13D, 16D, 11D),
            Block.box(13D, 1D, 5D, 14D, 15D, 11D),
            Block.box(14D, 2D, 6D, 15D, 14D, 10D),
            Block.box(15D, 3D, 7D, 16D, 13D, 9D),
            Block.box(15D, 4D, 9D, 16D, 12D, 11D),
            Block.box(15D, 4D, 5D, 16D, 12D, 7D),
            Block.box(15D, 6D, 11D, 16D, 10D, 12D),
            Block.box(15D, 6D, 4D, 16D, 10D, 5D),
            Block.box(15D, 7D, 12D, 16D, 9D, 13D),
            Block.box(15D, 7D, 3D, 16D, 9D, 4D),
            Block.box(14D, 3D, 10D, 15D, 13D, 12D),
            Block.box(14D, 3D, 4D, 15D, 13D, 6D),
            Block.box(14D, 4D, 12D, 15D, 12D, 13D),
            Block.box(14D, 4D, 3D, 15D, 12D, 4D),
            Block.box(14D, 6D, 13D, 15D, 10D, 14D),
            Block.box(14D, 6D, 2D, 15D, 10D, 3D),
            Block.box(8D, 1D, 11D, 13D, 15D, 13D),
            Block.box(13D, 2D, 11D, 14D, 14D, 13D),
            Block.box(13D, 2D, 3D, 14D, 14D, 5D),
            Block.box(13D, 3D, 13D, 14D, 13D, 14D),
            Block.box(13D, 3D, 2D, 14D, 13D, 3D),
            Block.box(13D, 5D, 14D, 14D, 11D, 15D),
            Block.box(13D, 5D, 1D, 14D, 11D, 2D),
            Block.box(8D, 1D, 3D, 13D, 15D, 5D),
            Block.box(8D, 2D, 13D, 13D, 14D, 14D),
            Block.box(8D, 2D, 2D, 13D, 14D, 3D),
            Block.box(8D, 3D, 14D, 13D, 13D, 15D),
            Block.box(8D, 3D, 1D, 13D, 13D, 2D),
            Block.box(8D, 5D, 15D, 13D, 11D, 16D),
            Block.box(8D, 5D, 0D, 13D, 11D, 1D)
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
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof EnergyLiquifierTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }

        EnergyLiquifierTileEntity energyLiquifier = (EnergyLiquifierTileEntity) te;

        TileEntityContainerProvider.openGui(player, energyLiquifier, (i, playerInventory, playerEntity) -> new EnergyLiquifierContainer(i, playerInventory, energyLiquifier));

        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(HorizontalRotatableBlock.FACING));
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof EnergyLiquifierTileEntity) {
            EnergyLiquifierTileEntity energyLiquifier = (EnergyLiquifierTileEntity) te;
            Containers.dropContents(worldIn, pos, energyLiquifier.getInventory());
            Containers.dropContents(worldIn, pos, energyLiquifier.getUpgradeInventory());
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public InteractionResult addUpgrade(@Nullable Player player, ItemStack stack, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof EnergyLiquifierTileEntity) {
            EnergyLiquifierTileEntity liquifier = (EnergyLiquifierTileEntity) te;
            if (liquifier.getUpgradeInventory().getItem(0).isEmpty()) {
                liquifier.getUpgradeInventory().setItem(0, stack.copy().split(1));
                ItemUtils.decrItemStack(stack, player);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyLiquifierTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level1, BlockState state, BlockEntityType<T> type) {
        return new SimpleBlockEntityTicker<>();
    }
}
