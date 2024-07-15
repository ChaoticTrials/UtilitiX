package de.melanx.utilitix.content;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.UtilitiXConfig;
import de.melanx.utilitix.network.ItemEntityRepaired;
import de.melanx.utilitix.util.BoundingBoxUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class BetterMending {

    @SubscribeEvent
    public void pullXP(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel) {
            this.moveExps(event.level, ((ServerLevel) event.level).getEntities().getAll());
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void pullXPClient(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null) {
            this.moveExps(Minecraft.getInstance().level, Minecraft.getInstance().level.entitiesForRendering());
        }
    }

    private void moveExps(Level level, Iterable<Entity> entities) {
        if (!UtilitiXConfig.betterMending) return;
        for (Entity entity : entities) {
            if (!(entity instanceof ItemEntity item)) {
                continue;
            }

            ItemStack stack = item.getItem();
            if (stack.getDamageValue() <= 0 || stack.getEnchantmentLevel(Enchantments.MENDING) <= 0) {
                continue;
            }

            List<ExperienceOrb> xps = level.getEntitiesOfClass(ExperienceOrb.class, BoundingBoxUtils.expand(item, 7));
            for (ExperienceOrb orb : xps) {
                Vec3 vector = new Vec3(item.getX() - orb.getX(), item.getY() + (orb.getEyeHeight() / 2) - orb.getY(), item.getZ() - orb.getZ());
                if (vector.lengthSqr() < 0.2 && !level.isClientSide) {
                    int i = Math.min((int) (orb.getValue() * stack.getXpRepairRatio()), stack.getDamageValue());
                    stack.setDamageValue(stack.getDamageValue() - i);
                    orb.remove(Entity.RemovalReason.KILLED);

                    if (!stack.isDamaged()) {
                        UtilitiX.getNetwork().channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> item), new ItemEntityRepaired(item.getId()));
                        break;
                    }
                } else {
                    double scale = 1 - (vector.length() / 8);
                    orb.setDeltaMovement(orb.getDeltaMovement().add(vector.normalize().scale(scale * scale * 0.1)));
                }
            }
        }
    }
}
