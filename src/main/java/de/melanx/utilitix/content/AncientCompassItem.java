package de.melanx.utilitix.content;

import com.mojang.datafixers.util.Pair;
import de.melanx.utilitix.config.FeatureConfig;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.util.WorkerManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
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
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.jspecify.annotations.Nullable;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.mod.ModX;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class AncientCompassItem extends ItemBase {

    // One running search per player, so multiple holders search independently
    private static final Map<UUID, BiomeSearcher> ACTIVE = new ConcurrentHashMap<>();
    private static final Map<UUID, BlockPos> LAST_SEARCH_ORIGIN = new ConcurrentHashMap<>();
    private static final int RESEARCH_DISTANCE_SQR = 64 * 64;
    private static final Component TOOLTIP = Component.translatable("tooltip.utilitix.ancient_compass").withStyle(ChatFormatting.GRAY);

    public AncientCompassItem(ModX mod, Properties properties) {
        super(mod, properties);
        NeoForge.EVENT_BUS.addListener(AncientCompassItem::addToLootTable);
        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent _) -> {
            ACTIVE.clear();
            LAST_SEARCH_ORIGIN.clear();
        });
    }

    @Override
    public void inventoryTick(@Nonnull ItemStack stack, @Nonnull ServerLevel level, @Nonnull Entity entity, @Nullable EquipmentSlot slot) {
        if (!(entity instanceof Player player)) {
            return;
        }

        UUID id = player.getUUID();
        BiomeSearcher active = ACTIVE.get(id);

        if (active != null) {
            if (active.hasWork()) {
                return;
            }

            ACTIVE.remove(id);
            if (active.pair != null) {
                stack.set(ModDataComponentTypes.ancientCityPos, GlobalPos.of(level.dimension(), active.pair.getFirst()));
            } else {
                stack.remove(ModDataComponentTypes.ancientCityPos);
            }

            return;
        }

        if (level.getGameTime() % 20 != 0) {
            return;
        }

        GlobalPos storedPos = stack.get(ModDataComponentTypes.ancientCityPos);
        BlockPos lastOrigin = LAST_SEARCH_ORIGIN.get(id);

        boolean needsSearch = storedPos == null
                || !storedPos.isCloseEnough(level.dimension(), lastOrigin, RESEARCH_DISTANCE_SQR);

        if (!needsSearch) {
            return;
        }

        BlockPos origin = player.blockPosition();
        BiomeSearcher searcher = new BiomeSearcher(
                level,
                level.registryAccess()
                        .lookupOrThrow(Registries.STRUCTURE)
                        .getOrThrow(BuiltinStructures.ANCIENT_CITY),
                origin
        );

        ACTIVE.put(id, searcher);
        LAST_SEARCH_ORIGIN.put(id, origin);
        WorkerManager.addWorker(searcher);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> builder, @Nonnull TooltipFlag tooltipFlag) {
        builder.accept(TOOLTIP);
    }

    @Override
    public boolean isEnabled(@Nonnull FeatureFlagSet enabledFeatures) {
        return FeatureConfig.Items.ancientCompass;
    }

    public static void addToLootTable(LootTableLoadEvent event) {
        LootTable table = event.getTable();
        if (table.getLootTableId().equals(BuiltInLootTables.SIMPLE_DUNGEON.identifier())) {
            table.addPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(ModItems.ancientCompass))
                    .add(LootItem.lootTableItem(Items.AIR)
                            .setWeight(31))
                    .build());
        }
    }

    public static class BiomeSearcher implements WorkerManager.Worker {

        private int progress;
        @Nullable
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
                this.progress = 51;
                return false;
            }

            int chunkOriginX = SectionPos.blockToSectionCoord(this.startPos.getX());
            int chunkOriginZ = SectionPos.blockToSectionCoord(this.startPos.getZ());

            double minDistance = Double.MAX_VALUE;
            boolean foundSomething = false;
            int radius = this.progress++;
            for (StructurePlacement structurePlacement : this.placementsForStructure) {
                if (!(structurePlacement instanceof RandomSpreadStructurePlacement rndPlacement)) {
                    continue;
                }

                Pair<BlockPos, Holder<Structure>> nearestGeneratedStructure = ChunkGenerator.getNearestGeneratedStructure(
                        Set.of(this.structure), this.level, this.structureManager, chunkOriginX, chunkOriginZ,
                        radius, false, this.level.getSeed(), rndPlacement);
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
