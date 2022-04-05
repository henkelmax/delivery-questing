package de.maxhenkel.delivery.toast;

import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.chat.TranslatableComponent;

public class TaskCompletedToast extends TaskToast {

    public TaskCompletedToast(Task task) {
        super(task, new TranslatableComponent("toast.delivery.contract_completed"), true);
    }

}