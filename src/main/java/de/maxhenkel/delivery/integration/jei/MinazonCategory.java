package de.maxhenkel.delivery.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.Offer;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class MinazonCategory implements IRecipeCategory<Offer> {

    private IGuiHelper helper;

    public MinazonCategory(IGuiHelper helper) {
        this.helper = helper;
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
            public void draw(MatrixStack matrixStack, int x, int y) {
                Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(Main.MODID, "textures/gui/computer/minazon_icon.png"));
                AbstractGui.blit(matrixStack, x, y, 0, 0, 16, 16, 16, 16);
            }
        };
    }

    @Override
    public void setIngredients(Offer offer, IIngredients ingredients) {
        ingredients.setOutput(VanillaTypes.ITEM, offer.getItem());
    }

    @Override
    public String getTitle() {
        return new TranslationTextComponent("jei.delivery.minazon").getString();
    }

    @Override
    public ResourceLocation getUid() {
        return JEIPlugin.CATEGORY_MINAZON;
    }

    @Override
    public Class<? extends Offer> getRecipeClass() {
        return Offer.class;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Offer offer, IIngredients ingredients) {
        IGuiItemStackGroup group = layout.getItemStacks();
        group.init(0, false, 7, 13);
        group.set(0, offer.getItem());
    }

    @Override
    public void draw(Offer offer, MatrixStack matrixStack, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        ScreenBase.drawCentered(mc.fontRenderer, matrixStack, new TranslationTextComponent("jei.delivery.minazon_level", offer.getLevelRequirement()), 50, 1, 0xFFFFFF);
        mc.fontRenderer.func_243248_b(matrixStack, new TranslationTextComponent("jei.delivery.minazon_price", offer.getPrice()), 30, 18, ScreenBase.FONT_COLOR);
    }
}