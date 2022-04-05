package de.maxhenkel.delivery.integration.jei;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.gui.BulletinBoardScreen;
import de.maxhenkel.delivery.gui.ContractScreen;
import de.maxhenkel.delivery.gui.computer.ComputerScreen;
import de.maxhenkel.delivery.tasks.Offer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    public static final RecipeType<Offer> CATEGORY_MINAZON = RecipeType.create(Main.MODID, "minazon", Offer.class);
    public static final RecipeType<Boolean> CATEGORY_ENERGY_LIQUIFYING = RecipeType.create(Main.MODID, "energy_liquifying", Boolean.class);

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
        registration.addRecipes(CATEGORY_MINAZON, Main.OFFER_MANAGER.getOffers());
        registration.addRecipes(CATEGORY_ENERGY_LIQUIFYING, Arrays.asList(true, false));
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_1), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.barrel_tier_1.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_2), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.barrel_tier_2.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_3), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.barrel_tier_3.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_4), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.barrel_tier_4.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_5), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.barrel_tier_5.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_6), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.barrel_tier_6.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_1), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.cardboard_box_tier_1.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_2), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.cardboard_box_tier_2.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_3), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.cardboard_box_tier_3.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_4), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.cardboard_box_tier_4.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_5), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.cardboard_box_tier_5.description"));
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_6), VanillaTypes.ITEM_STACK, new TranslatableComponent("jei.delivery.cardboard_box_tier_6.description"));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BulletinBoardScreen.class, new TaskWidgetContainerHandler());
        registration.addGuiContainerHandler(ComputerScreen.class, new TaskWidgetContainerHandler());
        registration.addGuiContainerHandler(ContractScreen.class, new TaskWidgetContainerHandler());
    }
}
