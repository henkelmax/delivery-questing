package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessageBuyOffer implements Message<MessageBuyOffer> {

    private UUID offerID;

    public MessageBuyOffer(UUID offerID) {
        this.offerID = offerID;
    }

    public MessageBuyOffer() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        Progression progression = Main.getProgression(context.getSender());
        Group playerGroup;
        try {
            playerGroup = progression.getPlayerGroup(context.getSender().getUniqueID());
        } catch (Exception e) {
            return;
        }
        playerGroup.onBuyOffer(offerID);
    }

    @Override
    public MessageBuyOffer fromBytes(PacketBuffer packetBuffer) {
        offerID = packetBuffer.readUniqueId();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeUniqueId(offerID);
    }
}
