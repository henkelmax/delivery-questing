package de.maxhenkel.delivery.gui;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public abstract class BlockContainerProvider implements INamedContainerProvider {

    private Block block;

    public BlockContainerProvider(Block block) {
        this.block = block;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(block.getTranslationKey());
    }

}
