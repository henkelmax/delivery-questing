package de.maxhenkel.delivery.tasks;

import com.google.gson.Gson;
import de.maxhenkel.delivery.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OfferManager implements INBTSerializable<CompoundTag> {

    private List<Offer> offers;

    public OfferManager(List<Offer> offers) {
        this.offers = offers;
    }

    public OfferManager() {

    }

    public List<Offer> getOffers() {
        return offers;
    }

    @Nullable
    public Offer getOffer(UUID id) {
        return offers.stream().filter(offer -> offer.getId().equals(id)).findAny().orElse(null);
    }

    public List<Offer> getNewOffers(int levelFrom, int levelTo) {
        return offers.stream().filter(offer -> offer.getLevelRequirement() >= levelFrom && offer.getLevelRequirement() <= levelTo).collect(Collectors.toList());
    }

    public static OfferManager load() throws IOException {
        Path tasks = FMLPaths.CONFIGDIR.get().resolve(Main.MODID).resolve("offers.json");

        if (!tasks.toFile().exists()) {
            Main.LOGGER.warn("Could not find offers.json");
            Main.LOGGER.warn("Continuing with empty offers");
            return new OfferManager(new ArrayList<>());
        }

        Gson customGson = Deserializers.getGson();
        BufferedReader bufferedReader = Files.newBufferedReader(tasks);
        return customGson.fromJson(bufferedReader, OfferManager.class);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();

        ListTag offerList = new ListTag();
        for (Offer offer : offers) {
            offerList.add(offer.serializeNBT());
        }
        compound.put("Offers", offerList);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        ListTag offerList = compound.getList("Offers", 10);
        this.offers = new ArrayList<>();
        for (int i = 0; i < offerList.size(); i++) {
            Offer offer = new Offer();
            offer.deserializeNBT(offerList.getCompound(i));
            this.offers.add(offer);
        }
    }
}
