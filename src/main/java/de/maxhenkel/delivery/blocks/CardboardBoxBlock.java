package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.blocks.tileentity.CardboradBoxTileEntity;
import de.maxhenkel.delivery.gui.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CardboardBoxBlock extends HorizontalRotatableBlock implements IItemBlock, ITileEntityProvider {

    protected Tier tier;

    public CardboardBoxBlock(Tier tier) {
        super(Properties.create(Material.MISCELLANEOUS).sound(SoundType.PLANT).hardnessAndResistance(0.5F));
        this.tier = tier;
        setRegistryName(new ResourceLocation(Main.MODID, "cardboard_box_tier_" + tier.getTier()));
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().group(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (!(te instanceof CardboradBoxTileEntity)) {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        }

        CardboradBoxTileEntity cardboardBox = (CardboradBoxTileEntity) te;

        CardboardBoxContainer container;
        switch (tier) {
            case TIER_1:
                player.openContainer(new BlockContainerProvider(this) {
                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return new CardboardBoxContainerTier1(id, inventory, cardboardBox.getInventory());
                    }
                });
                break;
            case TIER_2:
                player.openContainer(new BlockContainerProvider(this) {
                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return new CardboardBoxContainerTier2(id, inventory, cardboardBox.getInventory());
                    }
                });
                break;
            case TIER_3:
                player.openContainer(new BlockContainerProvider(this) {
                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return new CardboardBoxContainerTier3(id, inventory, cardboardBox.getInventory());
                    }
                });
                break;
            case TIER_4:
                player.openContainer(new BlockContainerProvider(this) {
                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return new CardboardBoxContainerTier4(id, inventory, cardboardBox.getInventory());
                    }
                });
                break;
            case TIER_5:
                player.openContainer(new BlockContainerProvider(this) {
                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return new CardboardBoxContainerTier5(id, inventory, cardboardBox.getInventory());
                    }
                });
                break;
            case TIER_6:
                player.openContainer(new BlockContainerProvider(this) {
                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                        return new CardboardBoxContainerTier6(id, inventory, cardboardBox.getInventory());
                    }
                });
                break;
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new CardboradBoxTileEntity(tier);
    }

    public static enum Tier {

        TIER_1(1, 1), TIER_2(2, 4), TIER_3(3, 9), TIER_4(4, 18), TIER_5(5, 27), TIER_6(6, 64);

        private final int tier;
        private final int slotCount;

        Tier(int tier, int slotCount) {
            this.tier = tier;
            this.slotCount = slotCount;
        }

        public int getTier() {
            return tier;
        }

        public int getSlotCount() {
            return slotCount;
        }
    }

}
