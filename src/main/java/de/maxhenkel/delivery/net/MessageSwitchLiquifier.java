package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.gui.EnergyLiquifierContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSwitchLiquifier implements Message<MessageSwitchLiquifier> {

    public MessageSwitchLiquifier() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        Container container = context.getSender().openContainer;
        if (container instanceof EnergyLiquifierContainer) {
            EnergyLiquifierContainer energyLiquifier = (EnergyLiquifierContainer) container;
            energyLiquifier.getEnergyLiquifier().reverse();
        }
    }

    @Override
    public MessageSwitchLiquifier fromBytes(PacketBuffer packetBuffer) {
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {

    }
}
