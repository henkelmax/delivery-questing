package de.maxhenkel.delivery.integration.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TaskWidgetContainerHandler implements IGuiContainerHandler<AbstractContainerScreen<?>> {

    @Override
    public @Nullable Object getIngredientUnderMouse(AbstractContainerScreen<?> containerScreen, double mouseX, double mouseY) {
        if (!(containerScreen instanceof ITaskWidgetScreen)) {
            return null;
        }
        return ((ITaskWidgetScreen) containerScreen).getIngredientUnderMouse(mouseX, mouseY);
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(AbstractContainerScreen<?> containerScreen, double guiMouseX, double guiMouseY) {
        if (!(containerScreen instanceof ITaskWidgetScreen)) {
            return Collections.emptyList();
        }
        return ((ITaskWidgetScreen) containerScreen).getIngredients().stream().map(pair -> new IGuiClickableArea() {

            @Override
            public List<Component> getTooltipStrings() {
                return Collections.singletonList(new NoDisplayTextComponent());
            }

            @Override
            public Rect2i getArea() {
                return pair.getKey();
            }

            @Override
            public void onClick(IFocusFactory iFocusFactory, IRecipesGui iRecipesGui) {
                Object value = pair.getValue();
                if (value instanceof ItemStack itemStack) {
                    iRecipesGui.show(iFocusFactory.createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, itemStack));
                } else if (value instanceof FluidStack fluidStack) {
                    iRecipesGui.show(iFocusFactory.createFocus(RecipeIngredientRole.OUTPUT, ForgeTypes.FLUID_STACK, fluidStack));
                }
            }


        }).collect(Collectors.toList());
    }

}
