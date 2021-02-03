package de.maxhenkel.delivery;

import de.maxhenkel.corelib.config.ConfigBase;
import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig extends ConfigBase {

    public final ForgeConfigSpec.IntValue minComputerLevel;

    public ServerConfig(ForgeConfigSpec.Builder builder) {
        super(builder);
        minComputerLevel = builder
                .comment("The level when computers should be usable")
                .defineInRange("computer.min_level", 10, 1, Integer.MAX_VALUE);
    }

}
