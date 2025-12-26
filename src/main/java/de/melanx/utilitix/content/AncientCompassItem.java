package de.melanx.utilitix.content;

import com.mojang.datafixers.util.Pair;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.WorldWorkerManager;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public class AncientCompassItem extends ItemBase {

    private boolean isSearching = false;
    private BiomeSearcher biomeSearcher = null;
    private static final Component TOOLTIP = Component.translatable("tooltip.utilitix.ancient_compass").withStyle(ChatFormatting.GRAY);

    public AncientCompassItem(ModX mod, Properties properties) {
        super(mod, properties);
        NeoForge.EVENT_BUS.addListener(AncientCompassItem::addToLootTable);
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull Entity entity, int itemSlot, boolean isSelected) {
        if (level.isClientSide) {
            return;
        }

        BlockPos pos = stack.get(ModDataComponentTypes.ancientCityPos);

        ServerLevel serverLevel = (ServerLevel) level;
        if (((this.positionChanged(pos) && !this.isSearching) || !level.dimension().location().equals(stack.get(ModDataComponentTypes.ancientCityLevel))) && level.getGameTime() % 20 == 0) {
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
                    stack.set(ModDataComponentTypes.ancientCityPos, this.biomeSearcher.pair.getFirst());
                }

                stack.set(ModDataComponentTypes.ancientCityLevel, level.dimension().location());
            } else {
                stack.remove(ModDataComponentTypes.ancientCityPos);
                stack.remove(ModDataComponentTypes.ancientCityLevel);
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
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        tooltipComponents.add(TOOLTIP);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Items.ancientCompass;
    }

    public static void addToLootTable(LootTableLoadEvent event) {
        LootTable table = event.getTable();
        if (table.getLootTableId().equals(BuiltInLootTables.SIMPLE_DUNGEON.location())) {
            table.addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.ancientCompass))
                    .add(LootItem.lootTableItem(Items.AIR)
                            .setWeight(31))
                    .build());
        }
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
                if (nearestGeneratedStructure == null) {
                    continue;
                }

                foundSomething = true;
                double distance = this.startPos.distSqr(nearestGeneratedStructure.getFirst());
                if (distance < minDistance) {
                    minDistance = distance;
                    this.pair = nearestGeneratedStructure;
                }
            }

            return !foundSomething;
        }
    }
}
