package de.melanx.utilitix.content;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.network.ItemEntityRepairedSerializer;
import de.melanx.utilitix.util.BoundingBoxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class BetterMending {

    private static final Map<UUID, Boolean> ITEM_MENDING_MAP = Maps.newHashMap();

    @SubscribeEvent
    public void onEntityJoinsLevel(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof ItemEntity entity) {
            if (ITEM_MENDING_MAP.containsKey(entity.getUUID())) {
                return;
            }

            ItemStack item = entity.getItem();
            boolean hasMending = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, item) > 0;
            ITEM_MENDING_MAP.put(entity.getUUID(), hasMending);
        }
    }

    @SubscribeEvent
    public void onEntityLeavesLevel(EntityLeaveWorldEvent event) {
        Entity entity = event.getEntity();
        if (!ITEM_MENDING_MAP.containsKey(entity.getUUID())) {
            return;
        }

        ITEM_MENDING_MAP.remove(entity.getUUID());
    }

    @SubscribeEvent
    public void pullXP(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world instanceof ServerLevel level) {
            Set<ItemEntity> items = Sets.newHashSet();
            ITEM_MENDING_MAP.forEach((id, hasMending) -> {
                if (hasMending) {
                    Entity entity = level.getEntity(id);
                    if (entity instanceof ItemEntity i) {
                        items.add(i);
                    }
                }
            });

            this.moveExps(event.world, items.stream());
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void pullXPClient(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null) {
            Set<ItemEntity> items = Sets.newHashSet();

            for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
                Boolean hasMending = ITEM_MENDING_MAP.get(entity.getUUID());
                if (hasMending != null && hasMending) {
                    items.add((ItemEntity) entity);
                }
            }

            this.moveExps(Minecraft.getInstance().level, items.stream());
        }
    }

    private void moveExps(Level level, Stream<ItemEntity> entities) {
        if (!UtilitiXConfig.betterMending) return;
        entities.filter(e -> e.getItem().getDamageValue() > 0 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, e.getItem()) > 0)
                .forEach(item -> {
                    List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, BoundingBoxUtils.expand(item, 7));
                    for (ExperienceOrb orb : xps) {
                        Vec3 vector = new Vec3(item.getX() - orb.getX(), item.getY() + (orb.getEyeHeight() / 2) - orb.getY(), item.getZ() - orb.getZ());
                        if (vector.lengthSqr() < 0.2 && !level.isClientSide) {
                            ItemStack stack = item.getItem();
                            int i = Math.min((int) (orb.getValue() * stack.getXpRepairRatio()), stack.getDamageValue());
                            stack.setDamageValue(stack.getDamageValue() - i);
                            orb.remove(Entity.RemovalReason.KILLED);

                            if (!stack.isDamaged()) {
                                UtilitiX.getNetwork().channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> item), new ItemEntityRepairedSerializer.ItemEntityRepairedMessage(item.getId()));
                            }
                        } else {
                            double scale = 1 - (vector.length() / 8);
                            orb.setDeltaMovement(orb.getDeltaMovement().add(vector.normalize().scale(scale * scale * 0.1)));
                        }
                    }
                });
    }
}
