package de.melanx.utilitix.content;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class AncientCompass extends ItemBase {

    private static final Component TOOLTIP = Component.translatable("tooltip.utilitix.ancient_compass").withStyle(ChatFormatting.GRAY);

    public AncientCompass(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (!level.isClientSide) {
            BlockPos pos = null;
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("AncientCityPos")) {
                pos = BlockPos.of(tag.getLong("AncientCityPos"));
            }

            ServerLevel serverLevel = (ServerLevel) level;
            Pair<BlockPos, Holder<Structure>> nearestMapStructure = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(serverLevel, HolderSet.direct(serverLevel.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).getHolderOrThrow(BuiltinStructures.ANCIENT_CITY)), entity.blockPosition(), 50, false);
            if (nearestMapStructure != null) {
                if (nearestMapStructure.getFirst() != pos) {
                    tag.putLong("AncientCityPos", nearestMapStructure.getFirst().asLong());
                }

                tag.putString("AncientCityLevel", level.dimension().location().toString());
            } else {
                tag.remove("AncientCityPos");
                tag.remove("AncientCityLevel");
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(@Nonnull Consumer<IItemRenderProperties> consumer) {
        ItemProperties.register(this, new ResourceLocation("angle"), new CompassItemPropertyFunction((level, stack, entity) -> {
            if (!stack.getOrCreateTag().contains("AncientCityPos") || !stack.getOrCreateTag().contains("AncientCityLevel")) {
                return null;
            }

            return GlobalPos.of(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(stack.getOrCreateTag().getString("AncientCityLevel"))), BlockPos.of(stack.getOrCreateTag().getLong("AncientCityPos")));
        }));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, List<Component> tooltips, @Nonnull TooltipFlag isAdvanced) {
        tooltips.add(TOOLTIP);
    }
}
