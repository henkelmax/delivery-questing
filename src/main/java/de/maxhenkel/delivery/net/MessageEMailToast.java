package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.tasks.email.EMail;
import de.maxhenkel.delivery.toast.EMailToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

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
            Minecraft.getInstance().getToastGui().add(new EMailToast(eMail));
        }
    }

    @Override
    public MessageEMailToast fromBytes(PacketBuffer packetBuffer) {
        eMail = EMail.deserialize(packetBuffer.readCompoundTag());
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeCompoundTag(eMail.serializeNBT());
    }
}
