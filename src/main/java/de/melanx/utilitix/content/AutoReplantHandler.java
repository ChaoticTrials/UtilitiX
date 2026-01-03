package de.melanx.utilitix.content;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.config.CommonConfig;
import de.melanx.utilitix.config.FeatureConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;

@EventBusSubscriber
public class AutoReplantHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onItemDespawn(ItemExpireEvent event) {
        ItemEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide) {
            return;
        }

        BlockPos pos = entity.blockPosition();
        ItemStack stack = entity.getItem();
        if (stack.getItem() instanceof BlockItem item && (item.getBlock().defaultBlockState().is(BlockTags.CROPS) || item.getBlock().defaultBlockState().is(BlockTags.SAPLINGS))) {
            if (!FeatureConfig.Misc.InWorldChanges.autoReplant || !CommonConfig.plantsOnDespawn.test(BuiltInRegistries.ITEM.getKey(item))) {
                return;
            }

            try {
                DirectionalPlaceContext context = new DirectionalPlaceContext(level, pos, Direction.DOWN, stack, Direction.UP);
                if (item.place(context) == InteractionResult.SUCCESS) {
                    level.setBlockAndUpdate(pos, item.getBlock().defaultBlockState());
                    return;
                }

                context = new DirectionalPlaceContext(level, pos.above(), Direction.DOWN, stack, Direction.UP);
                if (item.place(context) == InteractionResult.SUCCESS) {
                    level.setBlockAndUpdate(pos.above(), item.getBlock().defaultBlockState());
                }
            } catch (NullPointerException e) {
                UtilitiX.getInstance().logger.warn("Tried to place {} but was prevented.", item);
            }
        }
    }
}
