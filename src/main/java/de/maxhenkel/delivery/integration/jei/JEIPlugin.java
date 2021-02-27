package de.maxhenkel.delivery.integration.jei;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.gui.BulletinBoardScreen;
import de.maxhenkel.delivery.gui.ContractScreen;
import de.maxhenkel.delivery.gui.computer.ComputerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

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
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_1), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.barrel_tier_1.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_2), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.barrel_tier_2.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_3), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.barrel_tier_3.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_4), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.barrel_tier_4.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_5), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.barrel_tier_5.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.BARREL_TIER_6), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.barrel_tier_6.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_1), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.cardboard_box_tier_1.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_2), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.cardboard_box_tier_2.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_3), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.cardboard_box_tier_3.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_4), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.cardboard_box_tier_4.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_5), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.cardboard_box_tier_5.description").getString());
        registration.addIngredientInfo(new ItemStack(ModBlocks.CARDBOARD_BOX_TIER_6), VanillaTypes.ITEM, new TranslationTextComponent("jei.delivery.cardboard_box_tier_6.description").getString());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(BulletinBoardScreen.class, new TaskWidgetContainerHandler());
        registration.addGuiContainerHandler(ComputerScreen.class, new TaskWidgetContainerHandler());
        registration.addGuiContainerHandler(ContractScreen.class, new TaskWidgetContainerHandler());
    }
}
