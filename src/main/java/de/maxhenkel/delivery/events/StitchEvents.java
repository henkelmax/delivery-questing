package de.maxhenkel.delivery.events;

import de.maxhenkel.delivery.gui.UpgradeSlot;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraftforge.client.event.TextureStitchEvent;

public class StitchEvents {

    public static void onStitch(TextureStitchEvent.Pre event) {
        if (event.getMap().location().equals(PlayerContainer.BLOCK_ATLAS)) {
            event.addSprite(UpgradeSlot.UPGRADE_SLOT);
        }
    }

}
