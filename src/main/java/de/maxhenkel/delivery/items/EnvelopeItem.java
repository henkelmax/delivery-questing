package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.gui.EnvelopeContainer;
import de.maxhenkel.delivery.gui.ItemInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnvelopeItem extends SingleSlotInventoryItem {

    public EnvelopeItem() {
        super(new Item.Properties().stacksTo(1).tab(ModItemGroups.TAB_DELIVERY), 1);
        setRegistryName(new ResourceLocation(Main.MODID, "envelope"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);

        playerIn.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return stack.getHoverName();
            }

            @Override
            public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player playerEntity) {
                return new EnvelopeContainer(id, playerInventory, new ItemInventory(playerEntity, stack, 1) {
                    @Override
                    public SoundEvent getOpenSound() {
                        return SoundEvents.BOOK_PAGE_TURN;
                    }

                    @Override
                    public SoundEvent getCloseSound() {
                        return SoundEvents.BOOK_PAGE_TURN;
                    }
                });
            }
        });

        return InteractionResultHolder.success(stack);
    }

}
