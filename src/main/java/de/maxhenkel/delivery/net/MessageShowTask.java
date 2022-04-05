package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.gui.ContractContainer;
import de.maxhenkel.delivery.gui.containerprovider.TaskContainerProvider;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class MessageShowTask implements Message<MessageShowTask> {

    private UUID task;

    public MessageShowTask(UUID task) {
        this.task = task;
    }

    public MessageShowTask() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.DEDICATED_SERVER;
    }

    @Override
    public void executeServerSide(NetworkEvent.Context context) {
        try {
            Group group = Main.getProgression(context.getSender()).getPlayerGroup(context.getSender().getUUID());
            Task task = Main.TASK_MANAGER.getTask(this.task, group);

            if (task == null) {
                return;
            }

            TaskContainerProvider.openGui(context.getSender(), task, new TranslatableComponent(ModItems.CONTRACT.getDescriptionId()), ContractContainer::new);
        } catch (Exception e) {

        }
    }

    @Override
    public MessageShowTask fromBytes(FriendlyByteBuf packetBuffer) {
        task = packetBuffer.readUUID();
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeUUID(task);
    }
}
