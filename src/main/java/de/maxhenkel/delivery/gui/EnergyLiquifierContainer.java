package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.blocks.tileentity.EnergyLiquifierTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class EnergyLiquifierContainer extends ContainerBase {

    protected EnergyLiquifierTileEntity energyLiquifier;

    public EnergyLiquifierContainer(int id, PlayerInventory playerInventory, EnergyLiquifierTileEntity energyLiquifier) {
        super(Containers.ENERGY_LIQUIFIER_CONTAINER, id, playerInventory, energyLiquifier.getInventory());
        this.energyLiquifier = energyLiquifier;
        addSlot(new Slot(inventory, 0, 53, 36) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });
        addSlot(new Slot(inventory, 1, 107, 36) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return super.isItemValid(stack) && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
            }

            @Override
            public int getSlotStackLimit() {
                return 1;
            }
        });

        addPlayerInventorySlots();
        trackIntArray(energyLiquifier.getFields());
    }

    public EnergyLiquifierTileEntity getEnergyLiquifier() {
        return energyLiquifier;
    }

    @Override
    public int getInvOffset() {
        return 0;
    }

    @Override
    public int getInventorySize() {
        return 2;
    }

}