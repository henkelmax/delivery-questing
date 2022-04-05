package de.maxhenkel.delivery.integration.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Offer;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class MinazonCategory implements IRecipeCategory<Offer> {

    private static ResourceLocation ICON = new ResourceLocation(Main.MODID, "textures/gui/computer/minazon_icon.png");
    private IGuiHelper helper;

    public MinazonCategory(IGuiHelper helper) {
        this.helper = helper;
    }

    @Override
    public RecipeType<Offer> getRecipeType() {
        return JEIPlugin.CATEGORY_MINAZON;
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(new ResourceLocation(Main.MODID, "textures/gui/jei/minazon.png"), 0, 0, 100, 38);
    }

    @Override
    public IDrawable getIcon() {
        return new IDrawable() {
            @Override
            public int getWidth() {
                return 16;
            }

            @Override
            public int getHeight() {
                return 16;
            }

            @Override
            public void draw(PoseStack matrixStack, int x, int y) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                RenderSystem.setShaderTexture(0, ICON);
                GuiComponent.blit(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
            }
        };
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Offer recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 8, 14).addIngredient(VanillaTypes.ITEM_STACK, recipe.getStack());
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("jei.delivery.minazon");
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(Main.MODID, "minazon");
    }

    @Override
    public Class<? extends Offer> getRecipeClass() {
        return Offer.class;
    }

    @Override
    public void draw(Offer recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        ScreenBase.drawCentered(mc.font, stack, new TranslatableComponent("jei.delivery.minazon_level", recipe.getLevelRequirement()), 50, 1, 0xFFFFFF);
        mc.font.draw(stack, new TranslatableComponent("jei.delivery.minazon_price", recipe.getPrice()), 30, 18, ScreenBase.FONT_COLOR);
    }

}