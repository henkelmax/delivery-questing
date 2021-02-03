package de.maxhenkel.delivery.toast;

import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.util.text.TranslationTextComponent;

public class TaskCompletedToast extends TaskToast {

    public TaskCompletedToast(Task task) {
        super(task, new TranslationTextComponent("toast.delivery.contract_completed"), true);
    }

}