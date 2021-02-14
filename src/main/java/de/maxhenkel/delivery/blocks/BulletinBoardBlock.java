package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.DirectionalVoxelShape;
import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.sound.SoundUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.BulletinBoardTileEntity;
import de.maxhenkel.delivery.gui.BulletinBoardContainer;
import de.maxhenkel.delivery.gui.containerprovider.GroupContainerProvider;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class BulletinBoardBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, IGroupBlock {

    private static final DirectionalVoxelShape SHAPE = new DirectionalVoxelShape.Builder().direction(Direction.NORTH,
            Block.makeCuboidShape(0D, 0D, 15D, 16D, 16D, 16D),
            Block.makeCuboidShape(0D, 0D, 14D, 1D, 16D, 15D),
            Block.makeCuboidShape(15D, 0D, 14D, 16D, 16D, 15D),
            Block.makeCuboidShape(1D, 15D, 14D, 15D, 16D, 15D),
            Block.makeCuboidShape(1D, 0D, 14D, 15D, 1D, 15D)
    ).direction(Direction.SOUTH,
            Block.makeCuboidShape(16D, 0D, 1D, 0D, 16D, 0D),
            Block.makeCuboidShape(16D, 0D, 2D, 15D, 16D, 1D),
            Block.makeCuboidShape(1D, 0D, 2D, 0D, 16D, 1D),
            Block.makeCuboidShape(15D, 15D, 2D, 1D, 16D, 1D),
            Block.makeCuboidShape(15D, 0D, 2D, 1D, 1D, 1D)
    ).direction(Direction.WEST,
            Block.makeCuboidShape(15D, 0D, 16D, 16D, 16D, 0D),
            Block.makeCuboidShape(14D, 0D, 16D, 15D, 16D, 15D),
            Block.makeCuboidShape(14D, 0D, 1D, 15D, 16D, 0D),
            Block.makeCuboidShape(14D, 15D, 15D, 15D, 16D, 1D),
            Block.makeCuboidShape(14D, 0D, 15D, 15D, 1D, 1D)
    ).direction(Direction.EAST,
            Block.makeCuboidShape(1D, 0D, 0D, 0D, 16D, 16D),
            Block.makeCuboidShape(2D, 0D, 0D, 1D, 16D, 1D),
            Block.makeCuboidShape(2D, 0D, 15D, 1D, 16D, 16D),
            Block.makeCuboidShape(2D, 15D, 1D, 1D, 16D, 15D),
            Block.makeCuboidShape(2D, 0D, 1D, 1D, 1D, 15D)
    ).build();

    public static final IntegerProperty CONTRACTS = IntegerProperty.create("contracts", 0, 3);

    public BulletinBoardBlock() {
        super(Properties.create(Material.WOOD).sound(SoundType.WOOD).notSolid().hardnessAndResistance(1.5F));
        setRegistryName(new ResourceLocation(Main.MODID, "bulletin_board"));
        setDefaultState(getDefaultState().with(CONTRACTS, 0));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity p, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (!(te instanceof BulletinBoardTileEntity)) {
            return super.onBlockActivated(state, worldIn, pos, p, handIn, hit);
        }
        BulletinBoardTileEntity tileEntity = (BulletinBoardTileEntity) te;

        if (!(p instanceof ServerPlayerEntity)) {
            return ActionResultType.SUCCESS;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        Optional<Group> group = getGroup(worldIn, pos, p);
        if (group.isPresent()) {
            ItemStack heldItem = player.getHeldItem(handIn);
            if (heldItem.getItem() == ModItems.CONTRACT) {
                UUID task = ModItems.CONTRACT.getTask(heldItem);
                if (task != null) {
                    heldItem.setCount(heldItem.getCount() - 1);
                    if (group.get().canAcceptTask(task)) {
                        group.get().addTask(task);
                        player.world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, SoundUtils.getVariatedPitch(player.world));
                    } else {
                        player.sendStatusMessage(new TranslationTextComponent("message.delivery.contract_already_accepted"), true);
                    }
                    return ActionResultType.SUCCESS;
                }
            }

            GroupContainerProvider.openGui(player, tileEntity, group.get(), new TranslationTextComponent(getBlock().getTranslationKey()), BulletinBoardContainer::new);
        }
        return ActionResultType.SUCCESS;
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
        builder.add(CONTRACTS);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new BulletinBoardTileEntity();
    }

}
