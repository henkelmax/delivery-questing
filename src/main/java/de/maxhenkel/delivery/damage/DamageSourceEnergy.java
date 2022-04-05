package de.maxhenkel.delivery.damage;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceEnergy extends DamageSource {

    public static final DamageSourceEnergy DAMAGE_ENERGY = new DamageSourceEnergy();

    public DamageSourceEnergy() {
        super("electrocuted");
    }

    @Override
    public boolean isBypassInvul() {
        return false;
    }

    @Override
    public boolean isBypassMagic() {
        return true;
    }

    @Override
    public boolean scalesWithDifficulty() {
        return false;
    }

    @Override
    public boolean isBypassArmor() {
        return true;
    }

    @Override
    public boolean isExplosion() {
        return false;
    }

    @Override
    public boolean isFire() {
        return false;
    }

    @Override
    public boolean isMagic() {
        return false;
    }

    @Override
    public boolean isProjectile() {
        return false;
    }

}
