package de.maxhenkel.delivery.damage;

import net.minecraft.util.DamageSource;

public class DamageSourceEnergy extends DamageSource {

    public static final DamageSourceEnergy DAMAGE_ENERGY = new DamageSourceEnergy();

    public DamageSourceEnergy() {
        super("electrocuted");
    }

    @Override
    public boolean canHarmInCreative() {
        return false;
    }

    @Override
    public boolean isDamageAbsolute() {
        return true;
    }

    @Override
    public boolean isDifficultyScaled() {
        return false;
    }

    @Override
    public boolean isUnblockable() {
        return true;
    }

    @Override
    public boolean isExplosion() {
        return false;
    }

    @Override
    public boolean isFireDamage() {
        return false;
    }

    @Override
    public boolean isMagicDamage() {
        return false;
    }

    @Override
    public boolean isProjectile() {
        return false;
    }

}
