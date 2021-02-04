package de.maxhenkel.delivery.blocks;

import de.maxhenkel.delivery.Tier;
import de.maxhenkel.delivery.blocks.fluid.LiquidEnergyBlock;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;

public class ModBlocks {

    public static final CardboardBoxBlock CARDBOARD_BOX_TIER_1 = new CardboardBoxBlock(Tier.TIER_1);
    public static final CardboardBoxBlock CARDBOARD_BOX_TIER_2 = new CardboardBoxBlock(Tier.TIER_2);
    public static final CardboardBoxBlock CARDBOARD_BOX_TIER_3 = new CardboardBoxBlock(Tier.TIER_3);
    public static final CardboardBoxBlock CARDBOARD_BOX_TIER_4 = new CardboardBoxBlock(Tier.TIER_4);
    public static final CardboardBoxBlock CARDBOARD_BOX_TIER_5 = new CardboardBoxBlock(Tier.TIER_5);
    public static final CardboardBoxBlock CARDBOARD_BOX_TIER_6 = new CardboardBoxBlock(Tier.TIER_6);
    public static final BarrelBlock BARREL_TIER_1 = new BarrelBlock(Tier.TIER_1);
    public static final BarrelBlock BARREL_TIER_2 = new BarrelBlock(Tier.TIER_2);
    public static final BarrelBlock BARREL_TIER_3 = new BarrelBlock(Tier.TIER_3);
    public static final BarrelBlock BARREL_TIER_4 = new BarrelBlock(Tier.TIER_4);
    public static final BarrelBlock BARREL_TIER_5 = new BarrelBlock(Tier.TIER_5);
    public static final BarrelBlock BARREL_TIER_6 = new BarrelBlock(Tier.TIER_6);
    public static final MailboxPostBlock MAILBOX_POST = new MailboxPostBlock();
    public static final MailboxBlock MAILBOX = new MailboxBlock();
    public static final EnergyLiquifierBlock ENERGY_LIQUIFIER = new EnergyLiquifierBlock();
    public static final BulletinBoardBlock BULLETIN_BOARD = new BulletinBoardBlock();
    public static final ComputerBlock COMPUTER = new ComputerBlock();
    public static final DronePadBlock DRONE_PAD = new DronePadBlock();
    public static final PackagerBlock PACKAGER = new PackagerBlock();

    public static final FlowingFluidBlock LIQUID_ENERGY = new LiquidEnergyBlock();

    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                CARDBOARD_BOX_TIER_1,
                CARDBOARD_BOX_TIER_2,
                CARDBOARD_BOX_TIER_3,
                CARDBOARD_BOX_TIER_4,
                CARDBOARD_BOX_TIER_5,
                CARDBOARD_BOX_TIER_6,
                BARREL_TIER_1,
                BARREL_TIER_2,
                BARREL_TIER_3,
                BARREL_TIER_4,
                BARREL_TIER_5,
                BARREL_TIER_6,
                MAILBOX_POST,
                MAILBOX,
                ENERGY_LIQUIFIER,
                LIQUID_ENERGY,
                BULLETIN_BOARD,
                COMPUTER,
                DRONE_PAD,
                PACKAGER
        );
    }

    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                CARDBOARD_BOX_TIER_1.toItem(),
                CARDBOARD_BOX_TIER_2.toItem(),
                CARDBOARD_BOX_TIER_3.toItem(),
                CARDBOARD_BOX_TIER_4.toItem(),
                CARDBOARD_BOX_TIER_5.toItem(),
                CARDBOARD_BOX_TIER_6.toItem(),
                BARREL_TIER_1.toItem(),
                BARREL_TIER_2.toItem(),
                BARREL_TIER_3.toItem(),
                BARREL_TIER_4.toItem(),
                BARREL_TIER_5.toItem(),
                BARREL_TIER_6.toItem(),
                MAILBOX_POST.toItem(),
                MAILBOX.toItem(),
                ENERGY_LIQUIFIER.toItem(),
                BULLETIN_BOARD.toItem(),
                COMPUTER.toItem(),
                DRONE_PAD.toItem(),
                PACKAGER.toItem()
        );
    }

}
