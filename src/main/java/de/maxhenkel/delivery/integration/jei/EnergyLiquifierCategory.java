package de.maxhenkel.delivery.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.fluid.ModFluids;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class EnergyLiquifierCategory implements IRecipeCategory<Boolean> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/jei/energy_liquifier.png");

    private IGuiHelper helper;

    public EnergyLiquifierCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<Boolean> getRecipeType() {
        return JEIPlugin.CATEGORY_ENERGY_LIQUIFYING;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(TEXTURE, 0, 0, 83, 61);
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ENERGY_LIQUIFIER));
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Boolean reversed, IFocusGroup focuses) {
        builder
                .addSlot(reversed ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT, 62, 4)
                .setFluidRenderer(16000, false, 16, 53)
                .addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(ModFluids.LIQUID_ENERGY, 16000))
                .addTooltipCallback((recipeSlotView, tooltip) -> {
                    tooltip.add(new TranslatableComponent("tooltip.delivery.energy", 16000));
                });
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.delivery.energy_liquifying");
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Main.MODID, "energy_liquifying");
    }

    @Override
    public Class<? extends Boolean> getRecipeClass() {
        return Boolean.class;
    }

    @Override
    public void draw(Boolean reversed, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        if (reversed) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.setShaderTexture(0, TEXTURE);
            GuiComponent.blit(stack, 31, 23, 83, 0, 22, 15, 256, 256);
        }
    }

}