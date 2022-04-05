package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.tasks.Task;
import de.maxhenkel.delivery.toast.TaskCompletedToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class MessageTaskCompletedToast implements Message<MessageTaskCompletedToast> {

    private Task task;

    public MessageTaskCompletedToast(Task task) {
        this.task = task;
    }

    public MessageTaskCompletedToast() {

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
        Minecraft.getInstance().getToasts().addToast(new TaskCompletedToast(task));
    }

    @Override
    public MessageTaskCompletedToast fromBytes(FriendlyByteBuf packetBuffer) {
        task = new Task();
        task.deserializeNBT(packetBuffer.readNbt());
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeNbt(task.serializeNBT());
    }
}
