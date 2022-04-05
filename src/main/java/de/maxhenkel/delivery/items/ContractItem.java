package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.gui.ContractContainer;
import de.maxhenkel.delivery.gui.containerprovider.TaskContainerProvider;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ContractItem extends Item {

    public ContractItem() {
        super(new Properties().stacksTo(1).tab(ModItemGroups.TAB_DELIVERY));
        setRegistryName(new ResourceLocation(Main.MODID, "contract"));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslatableComponent("tooltip.delivery.contract").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player p, InteractionHand handIn) {
        ItemStack stack = p.getItemInHand(handIn);

        if (!stack.hasTag()) {
            return InteractionResultHolder.success(stack);
        }

        if (!(p instanceof ServerPlayer)) {
            return InteractionResultHolder.success(stack);
        }
        ServerPlayer player = (ServerPlayer) p;

        UUID taskID = getTask(stack);

        if (taskID == null) {
            return InteractionResultHolder.success(stack);
        }

        int level;
        try {
            level = (int) Main.getProgression(player).getPlayerGroup(player.getUUID()).getLevel();
        } catch (Exception e) {
            level = 0;
        }

        Task task = Main.TASK_MANAGER.getTask(taskID, level);

        if (task == null) {
            return InteractionResultHolder.success(stack);
        }

        TaskContainerProvider.openGui(player, task, getName(stack), ContractContainer::new);

        return InteractionResultHolder.success(stack);
    }

    public ItemStack setTask(ItemStack stack, UUID taskID) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putUUID("TaskID", taskID);
        return stack;
    }

    @Nullable
    public UUID getTask(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        }
        CompoundTag tag = stack.getTag();
        if (!tag.contains("TaskID")) {
            return null;
        }
        return tag.getUUID("TaskID");
    }

}
