package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.gui.EnvelopeContainer;
import de.maxhenkel.delivery.gui.ItemInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class EnvelopeItem extends SingleSlotInventoryItem {

    public EnvelopeItem() {
        super(new Item.Properties().maxStackSize(1).group(ModItemGroups.TAB_DELIVERY), 1);
        setRegistryName(new ResourceLocation(Main.MODID, "envelope"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        playerIn.openContainer(new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return stack.getDisplayName();
            }

            @Override
            public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                return new EnvelopeContainer(id, playerInventory, new ItemInventory(playerEntity, stack, 1) {
                    @Override
                    public SoundEvent getOpenSound() {
                        return SoundEvents.ITEM_BOOK_PAGE_TURN;
                    }

                    @Override
                    public SoundEvent getCloseSound() {
                        return SoundEvents.ITEM_BOOK_PAGE_TURN;
                    }
                });
            }
        });

        return ActionResult.resultSuccess(stack);
    }

}
