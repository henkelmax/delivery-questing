package de.maxhenkel.delivery.toast;

import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.util.text.TranslationTextComponent;

public class ChallengeToast extends TaskToast {

    public ChallengeToast(Task task) {
        super(task, new TranslationTextComponent("toast.delivery.challenge_contract"), false);
    }

}