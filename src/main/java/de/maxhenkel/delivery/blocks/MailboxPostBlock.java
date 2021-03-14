package de.maxhenkel.delivery.blocks;

import de.maxhenkel.corelib.block.IItemBlock;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class MailboxPostBlock extends Block implements IItemBlock {

    private VoxelShape SHAPE = Block.box(7D, 0D, 7D, 9D, 16D, 9D);

    public MailboxPostBlock() {
        super(Properties.of(Material.METAL).sound(SoundType.METAL).strength(1F).noOcclusion());
        setRegistryName(new ResourceLocation(Main.MODID, "mailbox_post"));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public Item toItem() {
        return new BlockItem(this, new Item.Properties().tab(ModItemGroups.TAB_DELIVERY)).setRegistryName(getRegistryName());
    }

}
