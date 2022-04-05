package de.maxhenkel.delivery.blocks;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.GroupTileEntity;
import de.maxhenkel.delivery.tasks.Group;
import de.maxhenkel.delivery.tasks.Progression;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IGroupBlock {

    default Optional<Group> getGroup(Level worldIn, BlockPos pos, Player p) {
        BlockEntity te = worldIn.getBlockEntity(pos);

        if (!(te instanceof GroupTileEntity)) {
            return Optional.empty();
        }

        if (!(p instanceof ServerPlayer)) {
            return Optional.empty();
        }
        ServerPlayer player = (ServerPlayer) p;

        GroupTileEntity groupTileEntity = (GroupTileEntity) te;
        Group playerGroup = null;

        try {
            playerGroup = Main.getProgression(player).getPlayerGroup(player.getUUID());
        } catch (Exception e) {

        }
        if (groupTileEntity.getGroupID() == null) {

            if (playerGroup == null) {
                player.sendMessage(
                        new TranslatableComponent("message.delivery.no_group")
                                .append(new TextComponent(" "))
                                .append(ComponentUtils.wrapInSquareBrackets(
                                        new TranslatableComponent("message.delivery.create_group")
                                ).withStyle((style) -> style
                                        .applyFormat(ChatFormatting.GREEN)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/group create <group_name> <group_password>"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("/group create <group_name> <group_password>")))
                                ))
                                .append(new TextComponent(" "))
                                .append(ComponentUtils.wrapInSquareBrackets(
                                        new TranslatableComponent("message.delivery.join_group")
                                ).withStyle((style) -> style
                                        .applyFormat(ChatFormatting.GREEN)
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/group join <group_name> <group_password>"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("/group join <group_name> <group_password>")))
                                ))
                        , Util.NIL_UUID);
                return Optional.empty();
            } else {
                groupTileEntity.setGroup(playerGroup.getId());
            }
        } else if (playerGroup != null && !groupTileEntity.getGroupID().equals(playerGroup.getId())) {
            player.sendMessage(new TranslatableComponent("message.delivery.no_member"), Util.NIL_UUID);
            return Optional.empty();
        } else if (playerGroup == null) {
            player.sendMessage(new TranslatableComponent("message.delivery.no_group"), Util.NIL_UUID);
            return Optional.empty();
        }

        return Optional.of(playerGroup);
    }

    default void setGroup(Level worldIn, BlockPos pos, @Nullable LivingEntity placer) {
        if (!(placer instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) placer;
        Progression progression = Main.getProgression(player);
        Group group;
        try {
            group = progression.getPlayerGroup(player.getUUID());
        } catch (Exception e) {
            return;
        }
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (!(te instanceof GroupTileEntity)) {
            return;
        }
        GroupTileEntity groupTileEntity = (GroupTileEntity) te;
        groupTileEntity.setGroup(group.getId());
    }

}
