package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class ModTileEntities {

    public static TileEntityType<CardboradBoxTileEntity> CARDBOARD_BOX;
    public static TileEntityType<BarrelTileEntity> BARREL;
    public static TileEntityType<MailboxTileEntity> MAILBOX;

    public static void registerTileEntities(RegistryEvent.Register<TileEntityType<?>> event) {
        CARDBOARD_BOX = TileEntityType.Builder.create(CardboradBoxTileEntity::new,
                ModBlocks.CARDBOARD_BOX_TIER_1,
                ModBlocks.CARDBOARD_BOX_TIER_2,
                ModBlocks.CARDBOARD_BOX_TIER_3,
                ModBlocks.CARDBOARD_BOX_TIER_4,
                ModBlocks.CARDBOARD_BOX_TIER_5,
                ModBlocks.CARDBOARD_BOX_TIER_6
        ).build(null);
        CARDBOARD_BOX.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_box"));
        event.getRegistry().register(CARDBOARD_BOX);

        BARREL = TileEntityType.Builder.create(BarrelTileEntity::new,
                ModBlocks.BARREL_TIER_1,
                ModBlocks.BARREL_TIER_2,
                ModBlocks.BARREL_TIER_3,
                ModBlocks.BARREL_TIER_4,
                ModBlocks.BARREL_TIER_5,
                ModBlocks.BARREL_TIER_6
        ).build(null);
        BARREL.setRegistryName(new ResourceLocation(Main.MODID, "barrel"));
        event.getRegistry().register(BARREL);

        MAILBOX = TileEntityType.Builder.create(MailboxTileEntity::new, ModBlocks.MAILBOX).build(null);
        MAILBOX.setRegistryName(new ResourceLocation(Main.MODID, "mailbox"));
        event.getRegistry().register(MAILBOX);

    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {

    }

}
