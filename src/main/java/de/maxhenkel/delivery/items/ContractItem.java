package de.maxhenkel.delivery.items;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.ModItemGroups;
import de.maxhenkel.delivery.gui.ContractContainer;
import de.maxhenkel.delivery.gui.containerprovider.TaskContainerProvider;
import de.maxhenkel.delivery.tasks.Task;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ContractItem extends Item {

    public ContractItem() {
        super(new Properties().maxStackSize(1).group(ModItemGroups.TAB_DELIVERY));
        setRegistryName(new ResourceLocation(Main.MODID, "contract"));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("tooltip.delivery.contract").mergeStyle(TextFormatting.GRAY));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity p, Hand handIn) {
        ItemStack stack = p.getHeldItem(handIn);

        if (!stack.hasTag()) {
            return ActionResult.resultSuccess(stack);
        }

        if (!(p instanceof ServerPlayerEntity)) {
            return ActionResult.resultSuccess(stack);
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        UUID taskID = getTask(stack);

        if (taskID == null) {
            return ActionResult.resultSuccess(stack);
        }

        int level;
        try {
            level = (int) Main.getProgression(player).getPlayerGroup(player.getUniqueID()).getLevel();
        } catch (Exception e) {
            level = 0;
        }

        Task task = Main.TASK_MANAGER.getTask(taskID, level);

        if (task == null) {
            return ActionResult.resultSuccess(stack);
        }

        TaskContainerProvider.openGui(player, task, getDisplayName(stack), ContractContainer::new);

        return ActionResult.resultSuccess(stack);
    }

    public ItemStack setTask(ItemStack stack, UUID taskID) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putUniqueId("TaskID", taskID);
        return stack;
    }

    @Nullable
    public UUID getTask(ItemStack stack) {
        if (!stack.hasTag()) {
            return null;
        }
        CompoundNBT tag = stack.getTag();
        if (!tag.contains("TaskID")) {
            return null;
        }
        return tag.getUniqueId("TaskID");
    }

}
