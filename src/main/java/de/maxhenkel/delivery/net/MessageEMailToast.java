package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.email.EMail;
import de.maxhenkel.delivery.toast.EMailToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class MessageEMailToast implements Message<MessageEMailToast> {

    private EMail eMail;

    public MessageEMailToast(EMail eMail) {
        this.eMail = eMail;
    }

    public MessageEMailToast() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {
        runClient();
    }

    @OnlyIn(Dist.CLIENT)
    private void runClient() {
        if (eMail != null) {
            Minecraft.getInstance().getToasts().addToast(new EMailToast(eMail));
        }
    }

    @Override
    public MessageEMailToast fromBytes(FriendlyByteBuf packetBuffer) {
        eMail = EMail.deserialize(packetBuffer.readNbt(), new Group());
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeNbt(eMail.serializeNBT());
    }
}
