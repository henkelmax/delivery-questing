package de.maxhenkel.delivery.blocks;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.GroupTileEntity;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IGroupBlock {

    default Optional<Group> getGroup(World worldIn, BlockPos pos, PlayerEntity p) {
        TileEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof GroupTileEntity)) {
            return Optional.empty();
        }

        if (!(p instanceof ServerPlayerEntity)) {
            return Optional.empty();
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        GroupTileEntity groupTileEntity = (GroupTileEntity) te;
        Group playerGroup = null;

        try {
            playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
        } catch (Exception e) {

        }
        if (groupTileEntity.getGroupID() == null) {

            if (playerGroup == null) {
                player.sendMessage(
                        new TranslationTextComponent("message.delivery.no_group")
                                .append(new StringTextComponent(" "))
                                .append(TextComponentUtils.wrapInSquareBrackets(
                                        new TranslationTextComponent("message.delivery.create_group")
                                ).withStyle((style) -> style
                                        .applyFormat(TextFormatting.GREEN)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/group create <group_name> <group_password>"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("/group create <group_name> <group_password>")))
                                ))
                                .append(new StringTextComponent(" "))
                                .append(TextComponentUtils.wrapInSquareBrackets(
                                        new TranslationTextComponent("message.delivery.join_group")
                                ).withStyle((style) -> style
                                        .applyFormat(TextFormatting.GREEN)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/group join <group_name> <group_password>"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("/group join <group_name> <group_password>")))
                                ))
                        , Util.NIL_UUID);
                return Optional.empty();
            } else {
                groupTileEntity.setGroup(playerGroup.getId());
            }
        } else if (playerGroup != null && !groupTileEntity.getGroupID().equals(playerGroup.getId())) {
            player.sendMessage(new TranslationTextComponent("message.delivery.no_member"), Util.NIL_UUID);
            return Optional.empty();
        } else if (playerGroup == null) {
            player.sendMessage(new TranslationTextComponent("message.delivery.no_group"), Util.NIL_UUID);
            return Optional.empty();
        }

        return Optional.of(playerGroup);
    }

    default void setGroup(World worldIn, BlockPos pos, @Nullable LivingEntity placer) {
        if (!(placer instanceof ServerPlayerEntity)) {
            return;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) placer;
        Progression progression = Main.getProgression(player);
        Group group;
        try {
            group = progression.getPlayerGroup(player.getUUID());
        } catch (Exception e) {
            return;
        }
        TileEntity te = worldIn.getBlockEntity(pos);
        if (!(te instanceof GroupTileEntity)) {
            return;
        }
        GroupTileEntity groupTileEntity = (GroupTileEntity) te;
        groupTileEntity.setGroup(group.getId());
    }

}
