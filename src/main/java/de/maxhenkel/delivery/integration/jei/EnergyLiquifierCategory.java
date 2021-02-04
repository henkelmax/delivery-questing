package de.maxhenkel.delivery.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.fluid.ModFluids;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class EnergyLiquifierCategory implements IRecipeCategory<Boolean> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/jei/energy_liquifier.png");

    private IGuiHelper helper;
    private ScreenBase.HoverArea hoverArea;

    public EnergyLiquifierCategory(IGuiHelper helper) {
        this.helper = helper;
        hoverArea = new ScreenBase.HoverArea(6, 4, 16, 53);
    }

    @Override
    public IDrawable getBackground() {
        return helper.createDrawable(TEXTURE, 0, 0, 83, 61);
    }

    @Override
    public IDrawable getIcon() {
        return helper.createDrawableIngredient(new ItemStack(ModBlocks.ENERGY_LIQUIFIER));
    }

    @Override
    public void setIngredients(Boolean reversed, IIngredients ingredients) {
        if (reversed) {
            ingredients.setInput(VanillaTypes.FLUID, new FluidStack(ModFluids.LIQUID_ENERGY, 16000));
        } else {
            ingredients.setOutput(VanillaTypes.FLUID, new FluidStack(ModFluids.LIQUID_ENERGY, 16000));
        }
    }

    @Override
    public String getTitle() {
        return new TranslationTextComponent("jei.delivery.energy_liquifying").getString();
    }

    @Override
    public ResourceLocation getUid() {
        return JEIPlugin.CATEGORY_ENERGY_LIQUIFYING;
    }

    @Override
    public Class<? extends Boolean> getRecipeClass() {
        return Boolean.class;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, Boolean reversed, IIngredients ingredients) {
        IGuiFluidStackGroup group = layout.getFluidStacks();
        group.init(0, reversed, 62, 4, 16, 53, 16000, false, null);
        group.set(0, new FluidStack(ModFluids.LIQUID_ENERGY, 16000));
    }

    @Override
    public List<ITextComponent> getTooltipStrings(Boolean reversed, double mouseX, double mouseY) {
        List<ITextComponent> list = new ArrayList<>();
        if (hoverArea.isHovered(0, 0, (int) mouseX, (int) mouseY)) {
            list.add(new TranslationTextComponent("tooltip.delivery.energy", 16000));
        }
        return list;
    }

    @Override
    public void draw(Boolean reversed, MatrixStack matrixStack, double mouseX, double mouseY) {
        if (reversed) {
            Minecraft mc = Minecraft.getInstance();
            mc.getTextureManager().bindTexture(TEXTURE);
            AbstractGui.blit(matrixStack, 31, 23, 83, 0, 22, 15, 256, 256);
        }
    }
}