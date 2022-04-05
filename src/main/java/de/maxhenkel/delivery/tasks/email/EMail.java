package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.vertex.PoseStack;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EMail implements INBTSerializable<CompoundTag> {

    protected Group group;
    protected UUID id;
    protected boolean read;

    public EMail(Group group) {
        this.group = group;
        id = UUID.randomUUID();
    }

    public abstract boolean isValid();

    public abstract MutableComponent getTitle();

    public abstract MutableComponent getText();

    public abstract MutableComponent getSender();

    @OnlyIn(Dist.CLIENT)
    public abstract void renderIcon(PoseStack matrixStack, Group group);

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compound = new CompoundTag();
        compound.putByte("ID", id());
        compound.putUUID("EMailID", id);
        compound.putBoolean("Read", read);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundTag compound) {
        id = compound.getUUID("EMailID");
        read = compound.getBoolean("Read");
    }

    @Nullable
    public static EMail deserialize(CompoundTag compound, Group group) {
        byte id = compound.getByte("ID");
        Class<? extends EMail> mailClass = mailTypes.get(id);
        if (mailClass == null) {
            return null;
        }
        try {
            EMail eMail = mailClass.getDeclaredConstructor(Group.class).newInstance(group);
            eMail.deserializeNBT(compound);
            return eMail;
        } catch (Exception e) {
            return null;
        }
    }

    private static final Map<Byte, Class<? extends EMail>> mailTypes;

    static {
        mailTypes = new HashMap<>();
        mailTypes.put((byte) 0, ContractEMail.class);
        mailTypes.put((byte) 1, OfferEMail.class);
        mailTypes.put((byte) 2, QuestsFinishedEMail.class);
    }

    private byte id() {
        return mailTypes.entrySet().stream().filter(entry -> entry.getValue().equals(getClass())).map(Map.Entry::getKey).findAny().orElse((byte) -1);
    }

}
