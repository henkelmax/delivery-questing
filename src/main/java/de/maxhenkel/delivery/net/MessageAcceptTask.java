package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class MessageAcceptTask implements Message<MessageAcceptTask> {

    private UUID task;

    public MessageAcceptTask(UUID task) {
        this.task = task;
    }

    public MessageAcceptTask() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        Task task = Main.TASK_MANAGER.getTask(this.task);

        if (task == null) {
            return;
        }

        try {
            Group playerGroup = Main.getProgression(context.getSender()).getPlayerGroup(context.getSender().getUniqueID());
            playerGroup.addTask(task.getId());
        } catch (Exception e) {
        }
    }

    @Override
    public MessageAcceptTask fromBytes(PacketBuffer packetBuffer) {
        task = packetBuffer.readUniqueId();
        return this;
    }

    @Override
    public void toBytes(PacketBuffer packetBuffer) {
        packetBuffer.writeUniqueId(task);
    }
}
