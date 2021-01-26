package de.maxhenkel.delivery.gui;

import de.maxhenkel.corelib.ClientRegistry;
import de.maxhenkel.corelib.inventory.ContainerFactoryTileEntity;
import de.maxhenkel.delivery.Main;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;

public class Containers {

    public static ContainerType<CardboardBoxContainer> CARDBOARD_TIER_1_CONTAINER;
    public static ContainerType<CardboardBoxContainer> CARDBOARD_TIER_2_CONTAINER;
    public static ContainerType<CardboardBoxContainer> CARDBOARD_TIER_3_CONTAINER;
    public static ContainerType<CardboardBoxContainer> CARDBOARD_TIER_4_CONTAINER;
    public static ContainerType<CardboardBoxContainer> CARDBOARD_TIER_5_CONTAINER;
    public static ContainerType<CardboardBoxContainer> CARDBOARD_TIER_6_CONTAINER;
    public static ContainerType<MailboxContainer> MAILBOX_CONTAINER;
    public static ContainerType<EnergyLiquifierContainer> ENERGY_LIQUIFIER_CONTAINER;
    public static ContainerType<EnvelopeContainer> ENVELOPE_CONTAINER;
    public static ContainerType<ParcelContainer> PARCEL_CONTAINER;

    @OnlyIn(Dist.CLIENT)
    public static void clientSetup() {
        ClientRegistry.registerScreen(CARDBOARD_TIER_1_CONTAINER, CardboardBoxScreenTier1::new);
        ClientRegistry.registerScreen(CARDBOARD_TIER_2_CONTAINER, CardboardBoxScreenTier2::new);
        ClientRegistry.registerScreen(CARDBOARD_TIER_3_CONTAINER, CardboardBoxScreenTier3::new);
        ClientRegistry.registerScreen(CARDBOARD_TIER_4_CONTAINER, CardboardBoxScreenTier4::new);
        ClientRegistry.registerScreen(CARDBOARD_TIER_5_CONTAINER, CardboardBoxScreenTier5::new);
        ClientRegistry.registerScreen(CARDBOARD_TIER_6_CONTAINER, CardboardBoxScreenTier6::new);
        ClientRegistry.registerScreen(MAILBOX_CONTAINER, MailboxScreen::new);
        ClientRegistry.registerScreen(ENERGY_LIQUIFIER_CONTAINER, EnergyLiquifierScreen::new);
        ClientRegistry.registerScreen(ENVELOPE_CONTAINER, EnvelopeScreen::new);
        ClientRegistry.registerScreen(PARCEL_CONTAINER, ParcelScreen::new);
    }

    public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
        CARDBOARD_TIER_1_CONTAINER = new ContainerType<>(CardboardBoxContainerTier1::new);
        CARDBOARD_TIER_1_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_tier_1"));
        event.getRegistry().register(CARDBOARD_TIER_1_CONTAINER);

        CARDBOARD_TIER_2_CONTAINER = new ContainerType<>(CardboardBoxContainerTier2::new);
        CARDBOARD_TIER_2_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_tier_2"));
        event.getRegistry().register(CARDBOARD_TIER_2_CONTAINER);

        CARDBOARD_TIER_3_CONTAINER = new ContainerType<>(CardboardBoxContainerTier3::new);
        CARDBOARD_TIER_3_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_tier_3"));
        event.getRegistry().register(CARDBOARD_TIER_3_CONTAINER);

        CARDBOARD_TIER_4_CONTAINER = new ContainerType<>(CardboardBoxContainerTier4::new);
        CARDBOARD_TIER_4_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_tier_4"));
        event.getRegistry().register(CARDBOARD_TIER_4_CONTAINER);

        CARDBOARD_TIER_5_CONTAINER = new ContainerType<>(CardboardBoxContainerTier5::new);
        CARDBOARD_TIER_5_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_tier_5"));
        event.getRegistry().register(CARDBOARD_TIER_5_CONTAINER);

        CARDBOARD_TIER_6_CONTAINER = new ContainerType<>(CardboardBoxContainerTier6::new);
        CARDBOARD_TIER_6_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "cardboard_tier_6"));
        event.getRegistry().register(CARDBOARD_TIER_6_CONTAINER);

        MAILBOX_CONTAINER = new ContainerType<>(MailboxContainer::new);
        MAILBOX_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "mailbox"));
        event.getRegistry().register(MAILBOX_CONTAINER);

        ENERGY_LIQUIFIER_CONTAINER = new ContainerType<>(new ContainerFactoryTileEntity<>(EnergyLiquifierContainer::new));
        ENERGY_LIQUIFIER_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "energy_liquifier"));
        event.getRegistry().register(ENERGY_LIQUIFIER_CONTAINER);

        ENVELOPE_CONTAINER = new ContainerType<>(EnvelopeContainer::new);
        ENVELOPE_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "envelope"));
        event.getRegistry().register(ENVELOPE_CONTAINER);

        PARCEL_CONTAINER = new ContainerType<>(ParcelContainer::new);
        PARCEL_CONTAINER.setRegistryName(new ResourceLocation(Main.MODID, "parcel"));
        event.getRegistry().register(PARCEL_CONTAINER);

    }

}
