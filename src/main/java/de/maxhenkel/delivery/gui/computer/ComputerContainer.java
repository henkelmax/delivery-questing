package de.maxhenkel.delivery.gui.computer;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.ComputerTileEntity;
import de.maxhenkel.delivery.gui.Containers;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;

import javax.annotation.Nullable;

public class ComputerContainer extends ContainerBase {

    private Group group;
    private int balance;
    private int level;
    @Nullable
    private ComputerTileEntity tileEntity;

    public ComputerContainer(int id, PlayerInventory playerInventory, TileEntity tileEntity, Group group) {
        super(Containers.COMPUTER_CONTAINER, id, playerInventory, null);
        if (tileEntity instanceof ComputerTileEntity) {
            this.tileEntity = (ComputerTileEntity) tileEntity;
        }
        this.group = group;

        addDataSlots(new IIntArray() {
            @Override
            public int get(int index) {
                if (playerInventory.player instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) playerInventory.player;
                    try {
                        Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
                        if (index == 0) {
                            return ((int) playerGroup.getBalance()) & 0xFFFF;
                        } else if (index == 1) {
                            return ((int) playerGroup.getBalance()) >> 16;
                        } else if (index == 2) {
                            return (int) playerGroup.getLevel() + 1;
                        }
                    } catch (Exception e) {
                        return 0;
                    }
                }
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    balance = (balance & 0xFFFF0000) | value & 0xFFFF;
                } else if (index == 1) {
                    balance = (balance & 0x0000FFFF) | value << 16;
                } else if (index == 2) {
                    level = value - 1;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    public void tick() {
        if (tileEntity != null) {
            tileEntity.containerTick();
        }
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        if (tileEntity != null) {
            return tileEntity.getEnergy().getEnergyStored() > 0;
        }
        return true;
    }

    public Group getGroup() {
        return group;
    }

    public int getBalance() {
        return balance;
    }

    public int getLevel() {
        return level;
    }
}
