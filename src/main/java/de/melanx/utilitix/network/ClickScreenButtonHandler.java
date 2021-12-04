package de.melanx.utilitix.network;

import de.melanx.utilitix.content.experiencecrystal.ScreenExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.util.XPUtils;
import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClickScreenButtonHandler {

    public static void handle(ClickScreenButtonHandler.Message msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;
            ServerLevel level = player.getLevel();
            ScreenExperienceCrystal.Button button = msg.button;
            BlockEntity be = level.getBlockEntity(msg.pos);

            if (be instanceof TileExperienceCrystal tile) {
                int playerXP = XPUtils.getExpPoints(player.experienceLevel, player.experienceProgress);

                switch (button) {
                    case ADD_ONE -> {
                        normalizeAddition(player, tile);
                        int xp = XPUtils.getXpBarCap(player.experienceLevel - 1);
                        int i = tile.addXp(xp);
                        player.giveExperiencePoints(-i);
                    }
                    case ADD_TEN -> {
                        normalizeAddition(player, tile);
                        int xp = 0;
                        for (int i = 0; i < 10; i++) {
                            xp += XPUtils.getXpBarCap(player.experienceLevel - 1 - i);
                        }
                        int i = tile.addXp(xp);
                        player.giveExperiencePoints(-i);
                    }
                    case ADD_ALL -> {
                        int xp = tile.addXp(playerXP);
                        player.giveExperiencePoints(-xp);
                    }
                    case SUB_ONE -> {
                        normalizeSubtraction(player, tile, 1);
                    }
                    case SUB_TEN -> {
                        normalizeSubtraction(player, tile, 10);
                    }
                    case SUB_ALL -> {
                        int xp = tile.subtractXp(Integer.MAX_VALUE);
                        player.giveExperiencePoints(xp);
                    }
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

    private static void normalizeAddition(Player player, TileExperienceCrystal tile) {
        int transfer = (int) (player.experienceProgress * player.getXpNeededForNextLevel());
        int i = tile.addXp(transfer);
        player.giveExperiencePoints(-i);
    }

    private static void normalizeSubtraction(Player player, TileExperienceCrystal tile, int levels) {
        int newV = XPUtils.getExpPoints(player.experienceLevel + levels, 0);
        int oldV = XPUtils.getExpPoints(player.experienceLevel, player.experienceProgress);
        int xp = newV - oldV;
        int i = tile.subtractXp(xp);
        player.giveExperiencePoints(i);
        if (Math.round(player.experienceProgress) == 1) {
            i = tile.subtractXp(1);
            player.giveExperiencePoints(i);
        }
    }

    public static class ClickScreenButtonSerializer implements PacketSerializer<Message> {

        @Override
        public Class<Message> messageClass() {
            return Message.class;
        }

        @Override
        public void encode(Message msg, FriendlyByteBuf buffer) {
            buffer.writeBlockPos(msg.pos);
            buffer.writeEnum(msg.button);
        }

        @Override
        public Message decode(FriendlyByteBuf buffer) {
            return new Message(buffer.readBlockPos(), buffer.readEnum(ScreenExperienceCrystal.Button.class));
        }
    }

    public static class Message {

        public final BlockPos pos;
        public final ScreenExperienceCrystal.Button button;

        public Message(BlockPos pos, ScreenExperienceCrystal.Button button) {
            this.pos = pos;
            this.button = button;
        }
    }
}
