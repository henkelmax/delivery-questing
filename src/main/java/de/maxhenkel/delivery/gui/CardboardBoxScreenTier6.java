package de.maxhenkel.delivery.gui;

import de.maxhenkel.delivery.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CardboardBoxScreenTier6 extends CardboardBoxScreen {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(Main.MODID, "textures/gui/container/cardboard_box_tier_6.png");

    public CardboardBoxScreenTier6(CardboardBoxContainer container, Inventory playerInventory, Component name) {
        super(BACKGROUND, container, playerInventory, name);
        imageHeight = 223;
    }

}
