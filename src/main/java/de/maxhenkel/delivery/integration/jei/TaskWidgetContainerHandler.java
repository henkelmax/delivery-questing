package de.maxhenkel.delivery.integration.jei;

import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskWidgetContainerHandler implements IGuiContainerHandler<ContainerScreen<?>> {

    @Nullable
    @Override
    public Object getIngredientUnderMouse(ContainerScreen<?> s, double mouseX, double mouseY) {
        if (!(s instanceof ITaskWidgetScreen)) {
            return null;
        }
        return ((ITaskWidgetScreen) s).getIngredientUnderMouse(mouseX, mouseY);
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(ContainerScreen<?> s, double mouseX, double mouseY) {
        if (!(s instanceof ITaskWidgetScreen)) {
            return Collections.emptyList();
        }
        return ((ITaskWidgetScreen) s).getIngredients().stream().map(pair -> new IGuiClickableArea() {

            @Override
            public List<ITextComponent> getTooltipStrings() {
                return Collections.singletonList(new NoDisplayTextComponent());
            }

            @Override
            public Rectangle2d getArea() {
                return pair.getKey();
            }

            @Override
            public void onClick(IFocusFactory iFocusFactory, IRecipesGui iRecipesGui) {
                iRecipesGui.show(iFocusFactory.createFocus(IFocus.Mode.OUTPUT, pair.getValue()));
            }


        }).collect(Collectors.toList());
    }
}
