package de.maxhenkel.delivery.integration.theoneprobe;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.BarrelTileEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileInfoProvider implements IProbeInfoProvider {

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(Main.MODID, "probeinfoprovider");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        BlockEntity te = level.getBlockEntity(iProbeHitData.getPos());


        if (te instanceof BarrelTileEntity) {
            if (probeMode.equals(ProbeMode.EXTENDED)) {
                BarrelTileEntity barrel = (BarrelTileEntity) te;
                //iProbeInfo.progress(barrel.getTank().getFluidAmount(), barrel.getTank().getCapacity(), iProbeInfo.defaultProgressStyle().suffix("mb"));
            }
        }
    }
}