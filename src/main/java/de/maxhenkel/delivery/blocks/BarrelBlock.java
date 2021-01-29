package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.block.VoxelUtils;
import de.maxhenkel.corelib.fluid.FluidUtils;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.BarrelTileEntity;
import de.maxhenkel.delivery.tasks.ITaskContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.List;

public class BarrelBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider, ITaskContainer {

    private static final VoxelShape SHAPE = VoxelUtils.combine(
            Block.makeCuboidShape(5D, 0D, 0D, 11D, 16D, 1D),
            Block.makeCuboidShape(3D, 0D, 1D, 13D, 16D, 2D),
            Block.makeCuboidShape(2D, 0D, 2D, 14D, 16D, 3D),
            Block.makeCuboidShape(1D, 0D, 3D, 15D, 16D, 5D),
            Block.makeCuboidShape(0D, 0D, 5D, 16D, 16D, 11D),
            Block.makeCuboidShape(1D, 0D, 11D, 15D, 16D, 13D),
            Block.makeCuboidShape(2D, 0D, 13D, 14D, 16D, 14D),
            Block.makeCuboidShape(3D, 0D, 14D, 13D, 16D, 15D),
            Block.makeCuboidShape(5D, 0D, 15D, 11D, 16D, 16D)
    );

    protected Tier tier;

    public BarrelBlock(Tier tier) {
        super(Properties.create(Material.IRON).sound(SoundType.METAL).notSolid().hardnessAndResistance(1.5F));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, "barrel_tier_" + tier.getTier()));

    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_DELIVERY).maxStackSize(1)).setRegistryName(getRegistryName());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        CompoundNBT blockEntityTag = stack.getChildTag("BlockEntityTag");
        if (blockEntityTag != null) {
            FluidTank tank = new FluidTank(Integer.MAX_VALUE).readFromNBT(blockEntityTag.getCompound("Fluid"));
            tooltip.add(new TranslationTextComponent("tooltip.delivery.fluid_type", tank.getFluid().getDisplayName()).mergeStyle(TextFormatting.GRAY));
            tooltip.add(new TranslationTextComponent("tooltip.delivery.fluid", tank.getFluidAmount()).mergeStyle(TextFormatting.GRAY));
        }
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (FluidUtils.tryFluidInteraction(player, handIn, worldIn, pos)) {
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new BarrelTileEntity(tier);
    }

    @Override
    public NonNullList<ItemStack> getItems(ItemStack stack) {
        return NonNullList.create();
    }

    @Override
    public NonNullList<FluidStack> getFluids(ItemStack stack) {
        NonNullList<FluidStack> fluids = NonNullList.create();
        CompoundNBT blockEntityTag = stack.getChildTag("BlockEntityTag");
        if (blockEntityTag != null) {
            FluidTank tank = new FluidTank(Integer.MAX_VALUE).readFromNBT(blockEntityTag.getCompound("Fluid"));
            fluids.add(tank.getFluid());
        }
        return fluids;
    }

    public static enum Tier {

        TIER_1(1, 1_000), TIER_2(2, 4_000), TIER_3(3, 16_000), TIER_4(4, 64_000), TIER_5(5, 256_000), TIER_6(6, 1_024_000);

        private final int tier;
        private final int millibuckets;

        Tier(int tier, int millibuckets) {
            this.tier = tier;
            this.millibuckets = millibuckets;
        }

        public int getTier() {
            return tier;
        }

        public int getMillibuckets() {
            return millibuckets;
        }
    }

}
