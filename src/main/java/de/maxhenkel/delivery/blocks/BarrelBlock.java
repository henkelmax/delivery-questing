package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.block.VoxelUtils;
import de.maxhenkel.corelib.fluid.FluidUtils;
import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.BarrelTileEntity;
import de.maxhenkel.delivery.tasks.ITaskContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.List;

public class BarrelBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, ITaskContainer, ITiered {

    private static final VoxelShape SHAPE = VoxelUtils.combine(
            Block.box(5D, 0D, 0D, 11D, 16D, 1D),
            Block.box(3D, 0D, 1D, 13D, 16D, 2D),
            Block.box(2D, 0D, 2D, 14D, 16D, 3D),
            Block.box(1D, 0D, 3D, 15D, 16D, 5D),
            Block.box(0D, 0D, 5D, 16D, 16D, 11D),
            Block.box(1D, 0D, 11D, 15D, 16D, 13D),
            Block.box(2D, 0D, 13D, 14D, 16D, 14D),
            Block.box(3D, 0D, 14D, 13D, 16D, 15D),
            Block.box(5D, 0D, 15D, 11D, 16D, 16D)
    );

    protected Tier tier;

    public BarrelBlock(Tier tier) {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).noOcclusion().strength(1.5F));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, "barrel_tier_" + tier.getTier()));

    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY).stacksTo(1)).setRegistryName(getRegistryName());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag blockEntityTag = stack.getTagElement("BlockEntityTag");
        if (blockEntityTag != null) {
            FluidTank tank = new FluidTank(Integer.MAX_VALUE).readFromNBT(blockEntityTag.getCompound("Fluid"));
            tooltip.add(new TranslatableComponent("tooltip.delivery.fluid_type", tank.getFluid().getDisplayName()).withStyle(ChatFormatting.GRAY));
            tooltip.add(new TranslatableComponent("tooltip.delivery.fluid", tank.getFluidAmount()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (FluidUtils.tryFluidInteraction(player, handIn, worldIn, pos)) {
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BarrelTileEntity(tier, blockPos, blockState);
    }

    @Override
    public NonNullList<FluidStack> getFluids(ItemStack stack) {
        NonNullList<FluidStack> fluids = NonNullList.withSize(1, FluidStack.EMPTY);
        CompoundTag blockEntityTag = stack.getTagElement("BlockEntityTag");
        if (blockEntityTag != null) {
            FluidTank tank = new FluidTank(Integer.MAX_VALUE).readFromNBT(blockEntityTag.getCompound("Fluid"));
            fluids.set(0, tank.getFluid());
        }
        return fluids;
    }

    @Override
    public int add(ItemStack stack, IFluidHandler handler, int amount) {
        CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
        CompoundTag fluidCompound = blockEntityTag.getCompound("Fluid");
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidCompound);
        if (fluid.isEmpty()) {
            FluidStack drain = handler.drain(Math.min(amount, getMillibuckets(getTier()) - fluid.getAmount()), IFluidHandler.FluidAction.EXECUTE);
            drain.writeToNBT(fluidCompound);
            blockEntityTag.put("Fluid", fluidCompound);
            return drain.getAmount();
        } else {
            FluidStack drain = handler.drain(new FluidStack(fluid, Math.min(amount, getMillibuckets(getTier()) - fluid.getAmount())), IFluidHandler.FluidAction.EXECUTE);
            fluid.grow(drain.getAmount());
            fluid.writeToNBT(fluidCompound);
            blockEntityTag.put("Fluid", fluidCompound);
            return drain.getAmount();
        }
    }

    @Override
    public boolean canAcceptFluids(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getFluids(stack).stream().map(FluidStack::getAmount).reduce(0, Integer::sum) >= getMillibuckets(tier);
    }

    public static int getMillibuckets(Tier tier) {
        switch (tier.getTier()) {
            case 1:
            default:
                return 1_000;
            case 2:
                return 4_000;
            case 3:
                return 16_000;
            case 4:
                return 64_000;
            case 5:
                return 256_000;
            case 6:
                return 1_024_000;
        }
    }

}
