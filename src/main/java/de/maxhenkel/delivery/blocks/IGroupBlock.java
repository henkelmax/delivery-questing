package de.maxhenkel.delivery.blocks;

import de.maxhenkel.delivery.Main;
import de.maxhenkel.delivery.blocks.tileentity.GroupTileEntity;
import de.maxhenkel.delivery.tasks.Group;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;

import java.util.function.Consumer;

public interface IGroupBlock {

    default ActionResultType checkGroup(World worldIn, BlockPos pos, PlayerEntity p, Consumer<Group> openGui) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (!(te instanceof GroupTileEntity)) {
            return ActionResultType.SUCCESS;
        }

        if (!(p instanceof ServerPlayerEntity)) {
            return ActionResultType.SUCCESS;
        }
        ServerPlayerEntity player = (ServerPlayerEntity) p;

        GroupTileEntity groupTileEntity = (GroupTileEntity) te;
        Group playerGroup = null;

        try {
            playerGroup = Main.getProgression(player).getGroup(player.getUniqueID());
        } catch (Exception e) {

        }
        if (groupTileEntity.getGroup() == null) {

            if (playerGroup == null) {
                player.sendMessage(
                        new TranslationTextComponent("message.delivery.no_group")
                                .append(new StringTextComponent(" "))
                                .append(TextComponentUtils.wrapWithSquareBrackets(
                                        new TranslationTextComponent("message.delivery.create_group")
                                ).modifyStyle((style) -> style
                                        .applyFormatting(TextFormatting.GREEN)
                                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/group create <group_name> <group_password>"))
                                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("/group create <group_name> <group_password>")))
                                ))
                                .append(new StringTextComponent(" "))
                                .append(TextComponentUtils.wrapWithSquareBrackets(
                                        new TranslationTextComponent("message.delivery.join_group")
                                ).modifyStyle((style) -> style
                                        .applyFormatting(TextFormatting.GREEN)
                                        .setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/group join <group_name> <group_password>"))
                                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("/group join <group_name> <group_password>")))
                                ))
                        , Util.DUMMY_UUID);
                return ActionResultType.SUCCESS;
            } else {
                groupTileEntity.setGroup(playerGroup.getId());
            }
        } else if (playerGroup != null && !groupTileEntity.getGroup().equals(playerGroup.getId())) {
            player.sendMessage(new TranslationTextComponent("message.delivery.no_member"), Util.DUMMY_UUID);
            return ActionResultType.SUCCESS;
        } else if (playerGroup == null) {
            player.sendMessage(new TranslationTextComponent("message.delivery.no_group"), Util.DUMMY_UUID);
            return ActionResultType.SUCCESS;
        }

        openGui.accept(playerGroup);
        return ActionResultType.SUCCESS;
    }

}
