package de.maxhenkel.delivery.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class DroneEntityPropellerBase extends Entity {

    private static final float PROPELLER_ROTATION = 128F;

    protected float propellerRotation;
    protected float propellerSpeed;

    public DroneEntityPropellerBase(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Override
    public void tick() {
        super.tick();

        propellerRotation += getPropellerRotationAmount();
    }

    public float getEnginePitch() {
        float pitch = Math.max(0.75F, propellerSpeed);
        if (isLoaded()) {
            pitch += 0.25F;
        }
        return pitch;
    }

    public void decreasePropellerSpeed() {
        propellerSpeed = Math.max(0F, propellerSpeed - 0.01F);
    }

    public void increasePropellerSpeed(float maxSpeed) {
        if (propellerSpeed > maxSpeed) {
            decreasePropellerSpeed();
            return;
        }
        propellerSpeed = Math.min(maxSpeed, propellerSpeed + 0.01F);
    }

    abstract boolean isLoaded();

    public float getPropellerRotationAmount() {
        return PROPELLER_ROTATION * propellerSpeed;
    }

    public float getPropellerRotation(float partialTicks) {
        return propellerRotation + getPropellerRotationAmount() * partialTicks;
    }

}
