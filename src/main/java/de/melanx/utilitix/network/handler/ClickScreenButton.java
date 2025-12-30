package de.melanx.utilitix.network.handler;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalBlockEntity;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalScreen;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.moddingx.libx.network.PacketHandler;

import javax.annotation.Nonnull;

public class ClickScreenButton extends PacketHandler<ClickScreenButton.Message> {

    public static final CustomPacketPayload.Type<Message> TYPE = new CustomPacketPayload.Type<>(UtilitiX.getInstance().resource("click_screen_button"));

    public ClickScreenButton() {
        super(TYPE, PacketFlow.SERVERBOUND, Message.CODEC, HandlerThread.MAIN);
    }

    @Override
    public void handle(Message msg, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel level = (ServerLevel) player.level();
        ExperienceCrystalScreen.Button button = msg.button;
        BlockEntity blockEntity = level.getBlockEntity(msg.pos);

        if (!(blockEntity instanceof ExperienceCrystalBlockEntity experienceCrystalBlockEntity)) {
            return;
        }

        int playerXP = XPUtils.getExpPoints(player.experienceLevel, player.experienceProgress);

        switch(button) {
            case ADD_ONE -> {
                normalizeAddition(player, experienceCrystalBlockEntity);
                int xp = XPUtils.getXpBarCap(player.experienceLevel - 1);
                int i = experienceCrystalBlockEntity.addXp(xp);
                player.giveExperiencePoints(-i);
            }
            case ADD_TEN -> {
                normalizeAddition(player, experienceCrystalBlockEntity);
                int xp = 0;
                for (int i = 0; i < 10; i++) {
                    xp += XPUtils.getXpBarCap(player.experienceLevel - 1 - i);
                }
                int i = experienceCrystalBlockEntity.addXp(xp);
                player.giveExperiencePoints(-i);
            }
            case ADD_ALL -> {
                int xp = experienceCrystalBlockEntity.addXp(playerXP < 0 ? Integer.MAX_VALUE : playerXP);
                player.giveExperiencePoints(-xp);
            }
            case SUB_ONE -> normalizeSubtraction(player, experienceCrystalBlockEntity, 1);
            case SUB_TEN -> normalizeSubtraction(player, experienceCrystalBlockEntity, 10);
            case SUB_ALL -> {
                int xp = experienceCrystalBlockEntity.subtractXp(Integer.MAX_VALUE);
                player.giveExperiencePoints(xp);
            }
        }
    }

    private static void normalizeAddition(Player player, ExperienceCrystalBlockEntity blockEntity) {
        int transfer = (int) (player.experienceProgress * player.getXpNeededForNextLevel());
        int i = blockEntity.addXp(transfer);
        player.giveExperiencePoints(-i);
    }

    private static void normalizeSubtraction(Player player, ExperienceCrystalBlockEntity blockEntity, int levels) {
        int newV = XPUtils.getExpPoints(player.experienceLevel + levels, 0);
        int oldV = XPUtils.getExpPoints(player.experienceLevel, player.experienceProgress);
        int xp = newV - oldV;
        int i = blockEntity.subtractXp(xp);

        player.giveExperiencePoints(i);
        if (Math.round(player.experienceProgress) == 1) {
            i = blockEntity.subtractXp(1);
            player.giveExperiencePoints(i);
        }
    }

    public record Message(BlockPos pos, ExperienceCrystalScreen.Button button) implements CustomPacketPayload {

        public static final StreamCodec<RegistryFriendlyByteBuf, Message> CODEC = StreamCodec.of(
                (buffer, msg) -> {
                    buffer.writeBlockPos(msg.pos);
                    buffer.writeEnum(msg.button);
                }, buffer -> new Message(buffer.readBlockPos(), buffer.readEnum(ExperienceCrystalScreen.Button.class))
        );

        @Nonnull
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ClickScreenButton.TYPE;
        }
    }
}
