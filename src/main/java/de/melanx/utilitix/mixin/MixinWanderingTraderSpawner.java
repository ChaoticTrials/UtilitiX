package de.melanx.utilitix.mixin;

import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mixin(WanderingTraderSpawner.class)
public abstract class MixinWanderingTraderSpawner {

    @Shadow
    @Nullable
    protected abstract BlockPos findSpawnPositionNear(LevelReader level, BlockPos checkPos, int i);

    @Shadow
    protected abstract boolean hasEnoughSpace(BlockGetter level, BlockPos possibleSpawnPos);

    @Shadow
    protected abstract void tryToSpawnLlamaFor(ServerLevel serverLevel, WanderingTrader trader, int maxDistance);

    @Inject(
            method = "spawn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void utilitix$spawn(ServerLevel level, CallbackInfoReturnable<Boolean> cir) {
        if (!FeatureConfig.Misc.InWorldChanges.wanderingTrader) {
            return;
        }

        List<ServerPlayer> players = level.getPlayers(player -> true);
        boolean returnValue = false;
        for (ServerPlayer player : players) {
            if (player == null) {
                continue;
            }

            if (level.getRandom().nextInt(10) > 1) {
                int i = 48;
                BlockPos playerPos = player.blockPosition();
                Optional<BlockPos> optional = level.getPoiManager().find((poiTypeHolder) -> poiTypeHolder.is(PoiTypes.MEETING), (pos) -> true, playerPos, i, PoiManager.Occupancy.ANY);
                BlockPos checkPos = optional.orElse(playerPos);
                BlockPos possibleSpawnPos = this.findSpawnPositionNear(level, checkPos, i);
                if (possibleSpawnPos != null && this.hasEnoughSpace(level, possibleSpawnPos)) {
                    if (level.getBiome(possibleSpawnPos).is(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                        continue;
                    }

                    WanderingTrader trader = EntityType.WANDERING_TRADER.spawn(level, null, null, possibleSpawnPos, EntitySpawnReason.EVENT, false, false);
                    if (trader != null) {
                        for (int j = 0; j < 2; ++j) {
                            this.tryToSpawnLlamaFor(level, trader, 4);
                        }

                        trader.setDespawnDelay(48000);
                        trader.setWanderTarget(checkPos);
                        trader.setHomeTo(checkPos, 16);
                        returnValue = true;
                    }
                }
            }
        }

        cir.setReturnValue(returnValue);
    }
}
