package de.maxhenkel.delivery;

import de.maxhenkel.corelib.CommonRegistry;
import de.maxhenkel.corelib.net.NetUtils;
import de.maxhenkel.delivery.blocks.ModBlocks;
import de.maxhenkel.delivery.blocks.tileentity.ModTileEntities;
import de.maxhenkel.delivery.capability.CapabilityEvents;
import de.maxhenkel.delivery.capability.ProgressionStorage;
import de.maxhenkel.delivery.commands.GroupCommand;
import de.maxhenkel.delivery.commands.TestCommand;
import de.maxhenkel.delivery.events.ContainerEvents;
import de.maxhenkel.delivery.fluid.ModFluids;
import de.maxhenkel.delivery.gui.Containers;
import de.maxhenkel.delivery.integration.IMC;
import de.maxhenkel.delivery.items.ModItems;
import de.maxhenkel.delivery.net.*;
import de.maxhenkel.delivery.tasks.OfferManager;
import de.maxhenkel.delivery.tasks.Progression;
import de.maxhenkel.delivery.tasks.TaskManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "delivery";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public static ServerConfig SERVER_CONFIG;
    public static ClientConfig CLIENT_CONFIG;

    public static SimpleChannel SIMPLE_CHANNEL;

    @CapabilityInject(Progression.class)
    public static Capability<Progression> PROGRESSION_CAPABILITY = null;

    public static TaskManager TASK_MANAGER;
    public static OfferManager OFFER_MANAGER;

    public Main() {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModBlocks::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, ModItems::registerItems);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, ModBlocks::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Fluid.class, ModFluids::registerFluids);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, ModTileEntities::registerTileEntities);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, Containers::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(IMC::enqueueIMC);

        SERVER_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.SERVER, ServerConfig.class);
        CLIENT_CONFIG = CommonRegistry.registerConfig(ModConfig.Type.CLIENT, ClientConfig.class);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(Main.this::clientSetup));
    }

    @SubscribeEvent
    public void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CapabilityEvents());
        MinecraftForge.EVENT_BUS.register(new ContainerEvents());

        SIMPLE_CHANNEL = CommonRegistry.registerChannel(Main.MODID, "default");
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 0, MessageSwitchLiquifier.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 1, MessageTaskCompletedToast.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 2, MessageShowTask.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 3, MessageSyncOffers.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 4, MessageSyncTasks.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 5, MessageBuyOffer.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 6, MessageAcceptTask.class);
        CommonRegistry.registerMessage(SIMPLE_CHANNEL, 7, MessageMarkEMailRead.class);

        CapabilityManager.INSTANCE.register(Progression.class, new ProgressionStorage(), Progression::new);
    }

    @SubscribeEvent
    public void serverStarted(FMLServerStartedEvent event) {
        try {
            TASK_MANAGER = TaskManager.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tasks", e);
        }
        try {
            OFFER_MANAGER = OfferManager.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load offers", e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void clientSetup(FMLClientSetupEvent event) {
        ModTileEntities.clientSetup();
        Containers.clientSetup();
    }

    @SubscribeEvent
    public void onPlayerLogIn(RegisterCommandsEvent event) {
        GroupCommand.register(event.getDispatcher());
        TestCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase.equals(TickEvent.Phase.START)) {
            TASK_MANAGER.onServerTick(ServerLifecycleHooks.getCurrentServer());
        }
    }

    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            NetUtils.sendTo(SIMPLE_CHANNEL, player, new MessageSyncOffers(OFFER_MANAGER));
            NetUtils.sendTo(SIMPLE_CHANNEL, player, new MessageSyncTasks(TASK_MANAGER));
        }
    }

    public static Progression getProgression(ServerPlayerEntity playerEntity) {
        return getProgression(playerEntity.server);
    }

    public static Progression getProgression(MinecraftServer server) {
        return server.getWorld(World.OVERWORLD).getCapability(PROGRESSION_CAPABILITY).orElseThrow(() -> new RuntimeException("Progression capability not found"));
    }

}
