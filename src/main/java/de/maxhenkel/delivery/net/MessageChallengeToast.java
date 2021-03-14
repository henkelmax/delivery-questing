package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.tasks.Task;
import de.maxhenkel.delivery.toast.ChallengeToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageChallengeToast implements Message<MessageChallengeToast> {

    private Task task;

    public MessageChallengeToast(Task task) {
        this.task = task;
    }

    public MessageChallengeToast() {

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
        Minecraft.getInstance().getToasts().addToast(new ChallengeToast(task));
    }

    @Override
    public MessageChallengeToast fromBytes(PacketBuffer packetBuffer) {
        task = new Task();
        task.deserializeNBT(packetBuffer.readNbt());
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeNbt(task.serializeNBT());
    }
}
