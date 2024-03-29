package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.TaskManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncTasks implements Message<MessageSyncTasks> {

    private TaskManager taskManager;

    public MessageSyncTasks(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public MessageSyncTasks() {

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
        Main.TASK_MANAGER = taskManager;
    }

    @Override
    public MessageSyncTasks fromBytes(FriendlyByteBuf packetBuffer) {
        taskManager = new TaskManager();
        taskManager.deserializeNBT(packetBuffer.readNbt());
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeNbt(taskManager.serializeNBT());
    }
}
