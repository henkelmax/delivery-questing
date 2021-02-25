package de.maxhenkel.delivery.tasks;

import de.maxhenkel.corelib.tag.SingleElementTag;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;
import java.util.stream.Collectors;

public class EndGameTask implements INBTSerializable<CompoundNBT> {

    private UUID id;
    private String contractorName;
    private String skin;
    private String profession;
    private int experience;
    private int money;
    private List<ItemElement> items;
    private List<FluidElement> fluids;
    private int base;
    private int offset;
    private double multiplier;

    public EndGameTask(UUID id, String contractorName, String skin, String profession, int experience, int money, List<ItemElement> items, List<FluidElement> fluids, int base, int offset, double multiplier) {
        this.id = id;
        this.contractorName = contractorName;
        this.skin = skin;
        this.profession = profession;
        this.experience = experience;
        this.money = money;
        this.items = items;
        this.fluids = fluids;
        this.base = base;
        this.offset = offset;
        this.multiplier = multiplier;
    }

    public EndGameTask() {
        this.items = new ArrayList<>();
        this.fluids = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public int getExperience() {
        return experience;
    }

    public List<FluidElement> getFluids() {
        return fluids;
    }

    public List<ItemElement> getItems() {
        return items;
    }

    public String getContractorName() {
        return contractorName;
    }

    public String getSkin() {
        return skin;
    }

    public String getProfession() {
        return profession;
    }

    public int getMoney() {
        return money;
    }

    public int getBase() {
        return base;
    }

    public int getOffset() {
        return offset;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Task toTask(int level) {
        String name = "";

        if (!items.isEmpty()) {
            if (items.get(0).item instanceof SingleElementTag) {
                SingleElementTag<Item> tag = (SingleElementTag<Item>) items.get(0).item;
                name = new TranslationTextComponent(tag.getElement().getTranslationKey()).getString();
            } else {
                name = localize(items.get(0).item.getName());
            }
        } else if (!fluids.isEmpty()) {
            if (fluids.get(0).item instanceof SingleElementTag) {
                SingleElementTag<Fluid> tag = (SingleElementTag<Fluid>) fluids.get(0).item;
                name = new TranslationTextComponent(new FluidStack(tag.getElement(), 1000).getTranslationKey()).getString();
            } else {
                name = localize(fluids.get(0).item.getName());
            }
        }

        List<ItemElement> weightedItems = new ArrayList<>();
        for (ItemElement element : items) {
            weightedItems.add(new ItemElement(element.getTag(), element.getItem(), calculateAmount(element.getAmount(), level)));
        }

        List<FluidElement> weightedFluids = new ArrayList<>();
        for (FluidElement element : fluids) {
            weightedFluids.add(new FluidElement(element.getTag(), element.getItem(), calculateAmount(element.getAmount(), level)));
        }

        return new Task(id, name, "", contractorName, skin, profession, 0, new ArrayList<>(), false, experience, money, weightedItems, weightedFluids, new ArrayList<>());
    }

    private String localize(ResourceLocation location) {
        String[] split = location.getPath().split("/");
        String str = split[split.length - 1];
        return Arrays.stream(str.split("_")).map(this::capitalize).collect(Collectors.joining(" "));
    }

    private String capitalize(String str) {
        return String.valueOf(str.charAt(0)).toUpperCase(Locale.ROOT) + str.substring(1);
    }

    private long calculateAmount(long amount, int level) {
        int offsetLevel = Math.max(level - base, 0);
        int mul = offsetLevel / Math.max(offset, 1);
        return Math.max((long) (Math.pow(multiplier, mul) * (double) amount), amount);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        compound.putUniqueId("ID", id);
        compound.putString("ContractorName", contractorName);
        compound.putString("Skin", skin);
        compound.putString("Profession", profession);

        compound.putInt("Experience", experience);
        compound.putInt("Money", money);

        ListNBT itemList = new ListNBT();
        for (ItemElement item : items) {
            itemList.add(item.serializeNBT());
        }
        compound.put("Items", itemList);

        ListNBT fluidList = new ListNBT();
        for (FluidElement fluid : fluids) {
            fluidList.add(fluid.serializeNBT());
        }
        compound.put("Fluids", fluidList);

        compound.putInt("Base", base);
        compound.putInt("Offset", offset);
        compound.putDouble("Multiplier", multiplier);

        return compound;
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        id = compound.getUniqueId("ID");
        contractorName = compound.getString("ContractorName");
        skin = compound.getString("Skin");
        profession = compound.getString("Profession");

        experience = compound.getInt("Experience");
        money = compound.getInt("Money");

        items = new ArrayList<>();
        ListNBT itemList = compound.getList("Items", 10);
        for (int i = 0; i < itemList.size(); i++) {
            CompoundNBT e = itemList.getCompound(i);
            ItemElement item = new ItemElement();
            item.deserializeNBT(e);
            items.add(item);
        }

        fluids = new ArrayList<>();
        ListNBT fluidList = compound.getList("Fluids", 10);
        for (int i = 0; i < fluidList.size(); i++) {
            CompoundNBT e = fluidList.getCompound(i);
            FluidElement fluid = new FluidElement();
            fluid.deserializeNBT(e);
            fluids.add(fluid);
        }

        base = compound.getInt("Base");
        offset = compound.getInt("Offset");
        multiplier = compound.getDouble("Multiplier");
    }
}
