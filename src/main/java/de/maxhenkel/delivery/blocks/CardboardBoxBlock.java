package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.corelib.inventory.TileEntityContainerProvider;
import de.maxhenkel.corelib.item.ItemUtils;
import de.maxhenkel.delivery.ITiered;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import de.maxhenkel.delivery.gui.*;
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
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

public class CardboardBoxBlock extends HorizontalRotatableBlock implements IItemBlock, EntityBlock, ITaskContainer, ITiered {

    protected Tier tier;

    public CardboardBoxBlock(Tier tier) {
        super(Properties.of(Material.DECORATION).sound(SoundType.GRASS).strength(0.5F));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, "cardboard_box_tier_" + tier.getTier()));
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
            NonNullList<ItemStack> itemStacks = NonNullList.withSize(getSlots(tier), ItemStack.EMPTY);
            ItemUtils.readInventory(blockEntityTag, "Items", itemStacks);
            tooltip.add(new TranslatableComponent("tooltip.delivery.stack_count", itemStacks.stream().filter(stack1 -> !stack1.isEmpty()).count()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Tier getTier() {
        return tier;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof CardboradBoxTileEntity)) {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }

        CardboradBoxTileEntity cardboardBox = (CardboradBoxTileEntity) te;

        switch (tier) {
            case TIER_1:
                TileEntityContainerProvider.openGui(player, cardboardBox, (i, playerInventory, playerEntity) -> new CardboardBoxContainerTier1(i, playerInventory, cardboardBox));
                break;
            case TIER_2:
                TileEntityContainerProvider.openGui(player, cardboardBox, (i, playerInventory, playerEntity) -> new CardboardBoxContainerTier2(i, playerInventory, cardboardBox));
                break;
            case TIER_3:
                TileEntityContainerProvider.openGui(player, cardboardBox, (i, playerInventory, playerEntity) -> new CardboardBoxContainerTier3(i, playerInventory, cardboardBox));
                break;
            case TIER_4:
                TileEntityContainerProvider.openGui(player, cardboardBox, (i, playerInventory, playerEntity) -> new CardboardBoxContainerTier4(i, playerInventory, cardboardBox));
                break;
            case TIER_5:
                TileEntityContainerProvider.openGui(player, cardboardBox, (i, playerInventory, playerEntity) -> new CardboardBoxContainerTier5(i, playerInventory, cardboardBox));
                break;
            case TIER_6:
                TileEntityContainerProvider.openGui(player, cardboardBox, (i, playerInventory, playerEntity) -> new CardboardBoxContainerTier6(i, playerInventory, cardboardBox));
                break;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CardboradBoxTileEntity(tier, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems(ItemStack stack) {
        NonNullList<ItemStack> inv = NonNullList.withSize(getSlots(tier), ItemStack.EMPTY);
        CompoundTag blockEntityTag = stack.getTagElement("BlockEntityTag");
        if (blockEntityTag != null) {
            ItemUtils.readInventory(blockEntityTag, "Items", inv);
        }
        return inv;
    }

    @Override
    public ItemStack add(ItemStack stack, ItemStack stackToAdd, int amount) {
        CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
        NonNullList<ItemStack> items = NonNullList.withSize(getSlots(tier), ItemStack.EMPTY);
        ItemUtils.readInventory(blockEntityTag, "Items", items);
        ItemStackHandler stackHandler = new ItemStackHandler(items);
        ItemStack result = ItemHandlerHelper.insertItem(stackHandler, stackToAdd.split(amount), false);
        ItemUtils.saveInventory(blockEntityTag, "Items", items);
        if (!result.isEmpty()) {
            stackToAdd.setCount(stackToAdd.getCount() + result.getCount());
        }
        return stackToAdd;
    }

    @Override
    public boolean canAcceptItems(ItemStack stack) {
        return true;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getItems(stack).stream().allMatch(stack1 -> !stack1.isEmpty() && stack1.getCount() >= stack1.getMaxStackSize());
    }

    public static int getSlots(de.maxhenkel.delivery.Tier tier) {
        switch (tier.getTier()) {
            case 1:
                return 1;
            case 2:
                return 4;
            case 3:
                return 9;
            case 4:
                return 18;
            case 5:
                return 27;
            case 6:
            default:
                return 54;
        }
    }

}
