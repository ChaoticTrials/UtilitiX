package de.melanx.utilitix.network;

import de.melanx.utilitix.content.experiencecrystal.ScreenExperienceCrystal;
import de.melanx.utilitix.content.experiencecrystal.TileExperienceCrystal;
import de.melanx.utilitix.util.XPUtils;
import io.github.noeppi_noeppi.libx.network.PacketSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ClickScreenButtonHandler {

    public static void handle(ClickScreenButtonHandler.Message msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity player = ctx.get().getSender();
            if (player == null)
                return;
            ServerWorld world = player.getServerWorld();
            ScreenExperienceCrystal.Button button = msg.button;
            TileEntity te = world.getTileEntity(msg.pos);

            if (te instanceof TileExperienceCrystal) {
                TileExperienceCrystal tile = (TileExperienceCrystal) te;
                int playerXP = XPUtils.getExpPoints(player.experienceLevel, player.experience);

                switch (button) {
                    case ADD_ONE: {
                        normalizeAddition(player, tile);
                        int xp = XPUtils.getXpBarCap(player.experienceLevel - 1);
                        int i = tile.addXp(xp);
                        player.giveExperiencePoints(-i);
                        break;
                    }
                    case ADD_TEN: {
                        normalizeAddition(player, tile);
                        int xp = 0;
                        for (int i = 0; i < 10; i++) {
                            xp += XPUtils.getXpBarCap(player.experienceLevel - 1 - i);
                        }
                        int i = tile.addXp(xp);
                        player.giveExperiencePoints(-i);
                        break;
                    }
                    case ADD_ALL: {
                        int xp = tile.addXp(playerXP);
                        player.giveExperiencePoints(-xp);
                        break;
                    }
                    case SUB_ONE: {
                        normalizeSubtraction(player, tile, 1);
                        break;
                    }
                    case SUB_TEN: {
                        normalizeSubtraction(player, tile, 10);
                        break;
                    }
                    case SUB_ALL:
                        int xp = tile.subtractXp(Integer.MAX_VALUE);
                        player.giveExperiencePoints(xp);
                        break;
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

    private static void normalizeAddition(PlayerEntity player, TileExperienceCrystal tile) {
        int transfer = (int) (player.experience * player.xpBarCap());
        int i = tile.addXp(transfer);
        player.giveExperiencePoints(-i);
    }

    private static void normalizeSubtraction(PlayerEntity player, TileExperienceCrystal tile, int levels) {
        int newV = XPUtils.getExpPoints(player.experienceLevel + levels, 0);
        int oldV = XPUtils.getExpPoints(player.experienceLevel, player.experience);
        int xp = newV - oldV;
        int i = tile.subtractXp(xp);
        player.giveExperiencePoints(i);
        if (Math.round(player.experience) == 1) {
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
        public void encode(Message msg, PacketBuffer buffer) {
            buffer.writeBlockPos(msg.pos);
            buffer.writeEnumValue(msg.button);
        }

        @Override
        public Message decode(PacketBuffer buffer) {
            return new Message(buffer.readBlockPos(), buffer.readEnumValue(ScreenExperienceCrystal.Button.class));
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
