package de.maxhenkel.delivery.integration.jei;

import de.maxhenkel.corelib.helpers.Pair;
import de.maxhenkel.delivery.gui.TaskWidget;
import net.minecraft.client.renderer.Rectangle2d;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public interface ITaskWidgetScreen {

    @Nullable
    TaskWidget getTaskWidget();

    default Object getIngredientUnderMouse(double mouseX, double mouseY) {
        TaskWidget taskWidget = getTaskWidget();
        if (taskWidget != null) {
            return taskWidget.getIngredientUnderMouse(mouseX, mouseY);
        } else {
            return null;
        }
    }

    default List<Pair<Rectangle2d, Object>> getIngredients() {
        TaskWidget taskWidget = getTaskWidget();
        if (taskWidget != null) {
            return taskWidget.getIngredients();
        } else {
            return Collections.emptyList();
        }
    }

}
