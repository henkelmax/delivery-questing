package de.maxhenkel.delivery.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import de.maxhenkel.delivery.Main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class DummyPlayer extends RemoteClientPlayerEntity {

    private static Map<String, ResourceLocation> cache = new HashMap<>();

    private ResourceLocation skin;

    public DummyPlayer(ClientWorld world, String filename, String name) {
        super(world, new GameProfile(new UUID(0L, 0L), name));

        loadSkin(filename, resourceLocation -> skin = resourceLocation);

        refreshDimensions();
    }

    public static void loadSkin(String filename, Consumer<ResourceLocation> consumer) {
        ResourceLocation location = new ResourceLocation(Main.MODID, "textures/entities/dynamic/" + filename + ".png");

        if (cache.containsKey(filename)) {
            consumer.accept(cache.get(filename));
            return;
        }

        File textureFile = FMLPaths.CONFIGDIR.get().resolve(Main.MODID).resolve("skins").resolve(filename + ".png").toFile();
        if (!textureFile.exists()) {
            return;
        }

        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        texturemanager.release(location);

        new Thread(() -> {
            try {
                NativeImage image = NativeImage.read(new FileInputStream(textureFile));
                RenderSystem.recordRenderCall(() -> {
                    texturemanager.register(location, new DynamicTexture(image));
                    cache.put(filename, location);
                    consumer.accept(location);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean isModelPartShown(PlayerModelPart part) {
        return true;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public ResourceLocation getSkinTextureLocation() {
        if (skin != null) {
            return skin;
        }
        return super.getSkinTextureLocation();
    }
}
