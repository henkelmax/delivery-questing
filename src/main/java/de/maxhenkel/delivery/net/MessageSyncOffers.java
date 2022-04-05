package de.maxhenkel.delivery.net;

import de.maxhenkel.corelib.net.Message;
import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.tasks.OfferManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncOffers implements Message<MessageSyncOffers> {

    private OfferManager offerManager;

    public MessageSyncOffers(OfferManager offerManager) {
        this.offerManager = offerManager;
    }

    public MessageSyncOffers() {

    }

    @Override
    public Dist getExecutingSide() {
        return Dist.CLIENT;
    }

    @Override
    public void executeClientSide(NetworkEvent.Context context) {
        runClient();
    }

    @OnlyIn(Dist.CLIENT)
    private void runClient() {
        Main.OFFER_MANAGER = offerManager;
    }

    @Override
    public MessageSyncOffers fromBytes(FriendlyByteBuf packetBuffer) {
        offerManager = new OfferManager();
        offerManager.deserializeNBT(packetBuffer.readNbt());
        return this;
    }

    @Override
    public void toBytes(FriendlyByteBuf packetBuffer) {
        packetBuffer.writeNbt(offerManager.serializeNBT());
    }
}
