package de.melanx.utilitix.content;

import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.network.handler.ItemEntityRepaired;
import de.melanx.utilitix.util.BoundingBoxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = "utilitix")
public class BetterMendingHandler {

    @SubscribeEvent
    public static void pullXP(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            BetterMendingHandler.moveExps(level, level.getEntities().getAll());
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void pullXPClient(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level != null) {
            ClientLevel level = Minecraft.getInstance().level;
            BetterMendingHandler.moveExps(level, level.entitiesForRendering());
        }
    }

    private static void moveExps(Level level, Iterable<Entity> entities) {
        if (!FeatureConfig.Misc.InWorldChanges.betterMending) return;
        List<ItemEntity> items = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof ItemEntity item) {
                items.add(item);
            }
        }

        for (ItemEntity item : items) {
            ItemStack stack = item.getItem();
            if (stack.getDamageValue() <= 0 || stack.getEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.MENDING)) <= 0) {
                continue;
            }

            List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, BoundingBoxUtils.expand(item, 7));
            for (ExperienceOrb orb : xps) {
                Vec3 vector = new Vec3(item.getX() - orb.getX(), item.getY() + (orb.getEyeHeight() / 2) - orb.getY(), item.getZ() - orb.getZ());
                if (vector.lengthSqr() >= 0.2 || level.isClientSide) {
                    double scale = 1 - (vector.length() / 8);
                    orb.setDeltaMovement(orb.getDeltaMovement().add(vector.normalize().scale(scale * scale * 0.1)));
                    continue;
                }

                int i = Math.min((int) (orb.getValue() * stack.getXpRepairRatio()), stack.getDamageValue());
                stack.setDamageValue(stack.getDamageValue() - i);
                orb.value -= (int) (i / stack.getXpRepairRatio());
                if (orb.value <= 0) {
                    orb.discard();
                }

                if (!stack.isDamaged()) {
                    PacketDistributor.sendToPlayersTrackingEntity(item, new ItemEntityRepaired.Message(item.getId()));
                    break;
                }
            }
        }
    }
}
