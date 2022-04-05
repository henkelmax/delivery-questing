package de.maxhenkel.delivery.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.Block;

public abstract class BlockContainerProvider implements MenuProvider {

    private Block block;

    public BlockContainerProvider(Block block) {
        this.block = block;
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(block.getDescriptionId());
    }

}
