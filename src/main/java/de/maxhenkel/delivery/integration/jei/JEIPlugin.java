package de.maxhenkel.delivery.integration.jei;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

@mezz.jei.api.JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static final ResourceLocation CATEGORY_MINAZON = new ResourceLocation(Main.MODID, "minazon");
    public static final ResourceLocation CATEGORY_ENERGY_LIQUIFYING = new ResourceLocation(Main.MODID, "energy_liquifying");

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Main.MODID, "delivery");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COMPUTER), CATEGORY_MINAZON);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ENERGY_LIQUIFIER), CATEGORY_ENERGY_LIQUIFYING);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MinazonCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EnergyLiquifierCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(Main.OFFER_MANAGER.getOffers(), CATEGORY_MINAZON);
        registration.addRecipes(Arrays.asList(true, false), CATEGORY_ENERGY_LIQUIFYING);
    }

}
