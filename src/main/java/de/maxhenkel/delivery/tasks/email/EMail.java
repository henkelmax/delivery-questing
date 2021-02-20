package de.maxhenkel.delivery.tasks.email;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class EMail implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private boolean read;

    public EMail() {
        id = UUID.randomUUID();
    }

    public abstract boolean isValid();

    public abstract IFormattableTextComponent getTitle();

    public abstract IFormattableTextComponent getText();

    public abstract IFormattableTextComponent getSender();

    @OnlyIn(Dist.CLIENT)
    public abstract void renderIcon(MatrixStack matrixStack, Group group);

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
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putByte("ID", id());
        compound.putUniqueId("EMailID", id);
        compound.putBoolean("Read", read);
        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        id = compound.getUniqueId("EMailID");
        read = compound.getBoolean("Read");
    }

    @Nullable
    public static EMail deserialize(CompoundNBT compound) {
        byte id = compound.getByte("ID");
        Class<? extends EMail> mailClass = mailTypes.get(id);
        if (mailClass == null) {
            return null;
        }
        try {
            EMail eMail = mailClass.newInstance();
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
    }

    private byte id() {
        return mailTypes.entrySet().stream().filter(entry -> entry.getValue().equals(getClass())).map(Map.Entry::getKey).findAny().orElse((byte) -1);
    }

}
