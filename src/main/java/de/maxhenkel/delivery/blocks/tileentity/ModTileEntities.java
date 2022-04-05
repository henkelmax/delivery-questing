package de.maxhenkel.delivery.blocks.tileentity;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.blocks.tileentity.render.BulletinBoardRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class ModTileEntities {

    public static BlockEntityType<CardboradBoxTileEntity> CARDBOARD_BOX;
    public static BlockEntityType<BarrelTileEntity> BARREL;
    public static BlockEntityType<MailboxTileEntity> MAILBOX;
    public static BlockEntityType<EnergyLiquifierTileEntity> ENERGY_LIQUIFIER;
    public static BlockEntityType<BulletinBoardTileEntity> BULLETIN_BOARD;
    public static BlockEntityType<ComputerTileEntity> COMPUTER;
    public static BlockEntityType<DronePadTileEntity> DRONE_PAD;
    public static BlockEntityType<PackagerTileEntity> PACKAGER;

    public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
        CARDBOARD_BOX = BlockEntityType.Builder.of(CardboradBoxTileEntity::new,
                ModBlocks.CARDBOARD_BOX_TIER_1,
                ModBlocks.CARDBOARD_BOX_TIER_2,
                ModBlocks.CARDBOARD_BOX_TIER_3,
                ModBlocks.CARDBOARD_BOX_TIER_4,
                ModBlocks.CARDBOARD_BOX_TIER_5,
                ModBlocks.CARDBOARD_BOX_TIER_6
        ).build(null);
        CARDBOARD_BOX.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_box"));
        event.getRegistry().register(CARDBOARD_BOX);

        BARREL = BlockEntityType.Builder.of(BarrelTileEntity::new,
                ModBlocks.BARREL_TIER_1,
                ModBlocks.BARREL_TIER_2,
                ModBlocks.BARREL_TIER_3,
                ModBlocks.BARREL_TIER_4,
                ModBlocks.BARREL_TIER_5,
                ModBlocks.BARREL_TIER_6
        ).build(null);
        BARREL.setRegistryName(new ResourceLocation(Main.MODID, "barrel"));
        event.getRegistry().register(BARREL);

        MAILBOX = BlockEntityType.Builder.of(MailboxTileEntity::new, ModBlocks.MAILBOX).build(null);
        MAILBOX.setRegistryName(new ResourceLocation(Main.MODID, "mailbox"));
        event.getRegistry().register(MAILBOX);

        ENERGY_LIQUIFIER = BlockEntityType.Builder.of(EnergyLiquifierTileEntity::new, ModBlocks.ENERGY_LIQUIFIER).build(null);
        ENERGY_LIQUIFIER.setRegistryName(new ResourceLocation(Main.MODID, "energy_liquifier"));
        event.getRegistry().register(ENERGY_LIQUIFIER);

        BULLETIN_BOARD = BlockEntityType.Builder.of(BulletinBoardTileEntity::new, ModBlocks.BULLETIN_BOARD).build(null);
        BULLETIN_BOARD.setRegistryName(new ResourceLocation(Main.MODID, "bulletin_board"));
        event.getRegistry().register(BULLETIN_BOARD);

        COMPUTER = BlockEntityType.Builder.of(ComputerTileEntity::new, ModBlocks.COMPUTER).build(null);
        COMPUTER.setRegistryName(new ResourceLocation(Main.MODID, "computer"));
        event.getRegistry().register(COMPUTER);

        DRONE_PAD = BlockEntityType.Builder.of(DronePadTileEntity::new, ModBlocks.DRONE_PAD).build(null);
        DRONE_PAD.setRegistryName(new ResourceLocation(Main.MODID, "drone_pad"));
        event.getRegistry().register(DRONE_PAD);

        PACKAGER = BlockEntityType.Builder.of(PackagerTileEntity::new, ModBlocks.PACKAGER).build(null);
        PACKAGER.setRegistryName(new ResourceLocation(Main.MODID, "packager"));
        event.getRegistry().register(PACKAGER);

    }

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        BlockEntityRenderers.register(ModTileEntities.BULLETIN_BOARD, BulletinBoardRenderer::new);
    }

}
