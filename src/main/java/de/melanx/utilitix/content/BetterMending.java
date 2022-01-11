package de.melanx.utilitix.content;

import com.google.common.collect.Streams;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.network.ItemEntityRepairedSerializer;
import de.melanx.utilitix.util.BoundingBoxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;
import java.util.stream.Stream;

public class BetterMending {

    @SubscribeEvent
    public void pullXP(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world instanceof ServerLevel) {
            this.moveExps(event.world, Streams.stream(((ServerLevel) event.world).getEntities().getAll()));
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void pullXPClient(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null) {
            this.moveExps(Minecraft.getInstance().level, Streams.stream(Minecraft.getInstance().level.entitiesForRendering()));
        }
    }

    private void moveExps(Level level, Stream<Entity> entities) {
        if (!UtilitiXConfig.betterMending) return;
        entities.filter(e -> e != null && e.getType() == EntityType.ITEM)
                .map(e -> (ItemEntity) e)
                .filter(e -> e.getItem().getDamageValue() > 0 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, e.getItem()) > 0)
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
