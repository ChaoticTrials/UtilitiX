package de.melanx.utilitix.module;

import com.google.common.collect.Streams;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.network.ItemEntityRepairedSerializer;
import io.github.noeppi_noeppi.libx.util.BoundingBoxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.stream.Stream;

public class BetterMending {

    @SubscribeEvent
    public void pullXP(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.world instanceof ServerWorld) {
            this.moveExps(event.world, ((ServerWorld) event.world).getEntities());
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void pullXPClient(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().world != null) {
            //noinspection UnstableApiUsage
            this.moveExps(Minecraft.getInstance().world, Streams.stream(Minecraft.getInstance().world.getAllEntities()));
        }
    }

    private void moveExps(World world, Stream<Entity> entities) {
        if (!UtilitiXConfig.betterMending) return;
        entities.filter(e -> e.getType() == EntityType.ITEM)
                .map(e -> (ItemEntity) e)
                .filter(e -> e.getItem().getDamage() > 0 && EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, e.getItem()) > 0)
                .forEach(item -> {
                    List<ExperienceOrbEntity> xps = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, BoundingBoxUtils.expand(item, 7));
                    for (ExperienceOrbEntity orb : xps) {
                        Vector3d vector = new Vector3d(item.getPosX() - orb.getPosX(), item.getPosY() + (orb.getEyeHeight() / 2) - orb.getPosY(), item.getPosZ() - orb.getPosZ());
                        if (vector.lengthSquared() < 0.2 && !world.isRemote) {
                            ItemStack stack = item.getItem();
                            int i = Math.min((int) (orb.getXpValue() * stack.getXpRepairRatio()), stack.getDamage());
                            stack.setDamage(stack.getDamage() - i);
                            orb.remove();

                            if (!stack.isDamaged()) {
                                UtilitiX.getNetwork().instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> item), new ItemEntityRepairedSerializer.ItemEntityRepairedMessage(item.getEntityId()));
                            }
                        } else {
                            double scale = 1 - (vector.length() / 8);
                            orb.setMotion(orb.getMotion().add(vector.normalize().scale(scale * scale * 0.1)));
                        }
                    }
                });
    }
}
