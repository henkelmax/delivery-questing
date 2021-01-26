package de.maxhenkel.delivery.integration.theoneprobe;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.BarrelTileEntity;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileInfoProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return Main.MODID + ":probeinfoprovider";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
        TileEntity te = world.getTileEntity(iProbeHitData.getPos());


        if (te instanceof BarrelTileEntity) {
            if (probeMode.equals(ProbeMode.EXTENDED)) {
                BarrelTileEntity barrel = (BarrelTileEntity) te;
                //iProbeInfo.progress(barrel.getTank().getFluidAmount(), barrel.getTank().getCapacity(), iProbeInfo.defaultProgressStyle().suffix("mb"));
            }
        }
    }
}