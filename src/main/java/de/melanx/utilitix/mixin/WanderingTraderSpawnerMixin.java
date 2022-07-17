package de.melanx.utilitix.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(WanderingTraderSpawner.class)
public abstract class WanderingTraderSpawnerMixin {

    @Shadow
    @Nullable
    protected abstract BlockPos findSpawnPositionNear(LevelReader p_35929_, BlockPos p_35930_, int p_35931_);

    @Shadow
    protected abstract boolean hasEnoughSpace(BlockGetter p_35926_, BlockPos p_35927_);

    @Shadow
    protected abstract void tryToSpawnLlamaFor(ServerLevel p_35918_, WanderingTrader p_35919_, int p_35920_);

    @Shadow
    @Final
    private ServerLevelData serverLevelData;

    @Inject(
            method = "spawn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void utilitix$spawn(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
        List<ServerPlayer> players = level.getPlayers(player -> true);
        boolean returnValue = false;
        for (ServerPlayer player : players) {
            if (player == null) {
                continue;
            }

            if (level.random.nextInt(10) > 1) {
                int i = 48;
                BlockPos playerPos = player.blockPosition();
                Optional<BlockPos> optional = level.getPoiManager().find((poiTypeHolder) -> poiTypeHolder.is(PoiTypes.MEETING), (pos) -> true, playerPos, i, PoiManager.Occupancy.ANY);
                BlockPos checkPos = optional.orElse(playerPos);
                BlockPos possibleSpawnPos = this.findSpawnPositionNear(level, checkPos, i);
                if (possibleSpawnPos != null && this.hasEnoughSpace(level, possibleSpawnPos)) {
                    if (level.getBiome(possibleSpawnPos).is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                        continue;
                    }

                    WanderingTrader trader = EntityType.WANDERING_TRADER.spawn(level, null, null, null, possibleSpawnPos, MobSpawnType.EVENT, false, false);
                    if (trader != null) {
                        for (int j = 0; j < 2; ++j) {
                            this.tryToSpawnLlamaFor(level, trader, 4);
                        }

                        this.serverLevelData.setWanderingTraderId(trader.getUUID());
                        trader.setDespawnDelay(48000);
                        trader.setWanderTarget(checkPos);
                        trader.restrictTo(checkPos, 16);
                        returnValue = true;
                    }
                }
            }
        }

        cir.setReturnValue(returnValue);
    }
}
