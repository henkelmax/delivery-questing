package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.advancements.ModTriggers;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

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
        try {
            Group playerGroup = Main.getProgression(context.getSender()).getPlayerGroup(context.getSender().getUUID());
            Task task = Main.TASK_MANAGER.getTask(this.task, playerGroup);
            if (task == null) {
                return;
            }
            playerGroup.addTask(task.getId());
            playerGroup.forEachOnlineMember(ModTriggers.ACCEPT_COMPUTER_CONTRACT_TRIGGER::trigger);
        } catch (Exception e) {
        }
    }

    @Override
    public MessageAcceptTask fromBytes(FriendlyByteBuf packetBuffer) {
        task = packetBuffer.readUUID();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeUUID(task);
    }
}
