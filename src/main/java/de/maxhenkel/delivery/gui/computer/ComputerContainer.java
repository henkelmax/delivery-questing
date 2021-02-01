package de.maxhenkel.delivery.gui.computer;

import de.maxhenkel.corelib.inventory.ContainerBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.gui.Containers;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.IIntArray;

public class ComputerContainer extends ContainerBase {

    private Group group;
    private int balance;
    private int level;

    public ComputerContainer(int id, PlayerInventory playerInventory, Group group) {
        super(Containers.COMPUTER_CONTAINER, id, playerInventory, null);
        this.group = group;


        trackIntArray(new IIntArray() {
            @Override
            public int get(int index) {
                if (playerInventory.player instanceof ServerPlayerEntity) {
                    ServerPlayerEntity player = (ServerPlayerEntity) playerInventory.player;
                    try {
                        Group playerGroup = Main.getProgression(player).getPlayerGroup(player.getUniqueID());
                        if (index == 0) {
                            return (int) playerGroup.getBalance();
                        } else if (index == 1) {
                            return (int) playerGroup.getLevel();
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
                    balance = value;
                } else if (index == 1) {
                    level = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        });
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
