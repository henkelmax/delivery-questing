package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Main;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class CardboardBoxScreenTier3 extends CardboardBoxScreen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/cardboard_box_tier_3.png");

    public CardboardBoxScreenTier3(CardboardBoxContainer container, PlayerInventory playerInventory, ITextComponent name) {
        super(BACKGROUND, container, playerInventory, name);
    }

}
