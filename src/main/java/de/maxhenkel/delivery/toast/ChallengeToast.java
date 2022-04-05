package de.maxhenkel.delivery.toast;

import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.network.chat.TranslatableComponent;

public class ChallengeToast extends TaskToast {

    public ChallengeToast(Task task) {
        super(task, new TranslatableComponent("toast.delivery.challenge_contract"), false);
    }

}