package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.gui.EnergyLiquifierContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

public class MessageSwitchLiquifier implements Message<MessageSwitchLiquifier> {

    public MessageSwitchLiquifier() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        AbstractContainerMenu container = context.getSender().containerMenu;
        if (container instanceof EnergyLiquifierContainer) {
            EnergyLiquifierContainer energyLiquifier = (EnergyLiquifierContainer) container;
            energyLiquifier.getEnergyLiquifier().reverse();
        }
    }

    @Override
    public MessageSwitchLiquifier fromBytes(FriendlyByteBuf packetBuffer) {
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {

    }
}
