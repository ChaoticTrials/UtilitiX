package de.melanx.utilitix.content;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.WorldWorkerManager;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class AncientCompass extends ItemBase {

    private boolean isSearching = false;
    private BiomeSearcher biomeSearcher = null;
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
            if (((this.positionChanged(pos) && !this.isSearching) || !tag.getString("AncientCityLevel").equals(level.dimension().location().toString())) && level.getGameTime() % 20 == 0) {
                this.isSearching = true;

                if (this.biomeSearcher != null) {
                    this.biomeSearcher.progress = 100; // to reach its limit, so it'd be removed -> only one biome searcher is present
                }

                this.biomeSearcher = new BiomeSearcher((ServerLevel) level, serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(BuiltinStructures.ANCIENT_CITY), entity.blockPosition());
                WorldWorkerManager.addWorker(this.biomeSearcher);
            }

            if (this.biomeSearcher != null && !this.biomeSearcher.hasWork()) {
                this.isSearching = false;

                if (this.biomeSearcher.pair != null) {
                    if (this.biomeSearcher.pair.getFirst() != pos) {
                        tag.putLong("AncientCityPos", this.biomeSearcher.pair.getFirst().asLong());
                    }

                    tag.putString("AncientCityLevel", level.dimension().location().toString());
                } else {
                    tag.remove("AncientCityPos");
                    tag.remove("AncientCityLevel");
                }
            }
        }
    }

    private boolean positionChanged(BlockPos pos) {
        if (this.biomeSearcher == null) {
            return true;
        }

        return !this.biomeSearcher.hasWork() && this.biomeSearcher.pair != null && this.biomeSearcher.pair.getFirst() != pos;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(@Nonnull Consumer<IClientItemExtensions> consumer) {
        ItemProperties.register(this, new ResourceLocation("angle"), new CompassItemPropertyFunction((level, stack, entity) -> {
            if (!stack.getOrCreateTag().contains("AncientCityPos") || !stack.getOrCreateTag().contains("AncientCityLevel")) {
                return null;
            }

            return GlobalPos.of(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(stack.getOrCreateTag().getString("AncientCityLevel"))), BlockPos.of(stack.getOrCreateTag().getLong("AncientCityPos")));
        }));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, List<Component> tooltips, @Nonnull TooltipFlag isAdvanced) {
        tooltips.add(TOOLTIP);
    }

    public static class BiomeSearcher implements WorldWorkerManager.IWorker {

        private int progress;
        private Pair<BlockPos, Holder<Structure>> pair = null;
        private final ServerLevel level;
        private final Holder<Structure> structure;
        private final BlockPos startPos;
        private final List<StructurePlacement> placementsForStructure;
        private final StructureManager structureManager;

        public BiomeSearcher(ServerLevel level, Holder<Structure> structure, BlockPos startPos) {
            this.level = level;
            this.structure = structure;
            this.startPos = startPos;
            this.placementsForStructure = this.level.getChunkSource().getGeneratorState().getPlacementsForStructure(this.structure);
            this.structureManager = this.level.structureManager();
            this.progress = 0;
        }

        @Override
        public boolean hasWork() {
            return this.progress <= 50 && this.pair == null;
        }

        @Override
        public boolean doWork() {
            if (this.placementsForStructure.isEmpty()) {
                return false;
            }

            int posX = SectionPos.blockToSectionCoord(this.startPos.getX());
            int posY = SectionPos.blockToSectionCoord(this.startPos.getY());

            double minDistance = Double.MAX_VALUE;
            boolean foundSomething = false;
            for (StructurePlacement structurePlacement : this.placementsForStructure) {
                RandomSpreadStructurePlacement rndPlacement = (RandomSpreadStructurePlacement) structurePlacement;
                Pair<BlockPos, Holder<Structure>> nearestGeneratedStructure = ChunkGenerator.getNearestGeneratedStructure(Set.of(this.structure), this.level, this.structureManager, posX, posY, this.progress++, false, this.level.getSeed(), rndPlacement);
                if (nearestGeneratedStructure != null) {
                    foundSomething = true;
                    double distance = this.startPos.distSqr(nearestGeneratedStructure.getFirst());
                    if (distance < minDistance) {
                        minDistance = distance;
                        this.pair = nearestGeneratedStructure;
                    }
                }
            }

            return !foundSomething;
        }
    }
}
