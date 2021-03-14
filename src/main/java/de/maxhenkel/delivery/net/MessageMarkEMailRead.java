package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessageMarkEMailRead implements Message<MessageMarkEMailRead> {

    private UUID mailID;

    public MessageMarkEMailRead(UUID mailID) {
        this.mailID = mailID;
    }

    public MessageMarkEMailRead() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        try {
            Group playerGroup = Main.getProgression(context.getSender()).getPlayerGroup(context.getSender().getUUID());
            playerGroup.markEMailRead(mailID);
        } catch (Exception e) {
        }
    }

    @Override
    public MessageMarkEMailRead fromBytes(PacketBuffer packetBuffer) {
        mailID = packetBuffer.readUUID();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeUUID(mailID);
    }
}
