package de.melanx.utilitix.content.wireless;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WirelessStorage extends SavedData {

    public static final String ID = UtilitiX.getInstance().modid + "_wireless";

    public static WirelessStorage get(Level level) {
        if (!level.isClientSide) {
            DimensionDataStorage storage = ((ServerLevel) level).getServer().overworld().getDataStorage();
            return storage.computeIfAbsent(nbt -> new WirelessStorage().load(nbt), WirelessStorage::new, ID);
        } else {
            return new WirelessStorage();
        }
    }

    private final Map<UUID, Map<GlobalPos, Integer>> signals = new HashMap<>();

    @Nonnull
    public WirelessStorage load(@Nonnull CompoundTag nbt) {
        this.signals.clear();
        ListTag list = nbt.getList("Signals", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            UUID uid = tag.getUUID("K");
            ListTag entries = tag.getList("V", Tag.TAG_COMPOUND);
            Map<GlobalPos, Integer> signalMap = new HashMap<>();
            for (int j = 0; j < entries.size(); j++) {
                CompoundTag cmp = entries.getCompound(j);
                try {
                    GlobalPos pos = GlobalPos.of(ResourceKey.create(Registry.DIMENSION_REGISTRY, Objects.requireNonNull(ResourceLocation.tryParse(cmp.getString("L")))), BlockPos.of(cmp.getLong("P")));
                    int strength = cmp.getInt("R");
                    signalMap.put(pos, strength);
                } catch (NullPointerException e) {
                    UtilitiX.getInstance().logger.warn("Invalid level loaded", e);
                }
            }
            this.signals.put(uid, signalMap);
        }

        return this;
    }

    @Nonnull
    @Override
    public CompoundTag save(@Nonnull CompoundTag compound) {
        ListTag list = new ListTag();
        for (Map.Entry<UUID, Map<GlobalPos, Integer>> entry : this.signals.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("K", entry.getKey());
            ListTag entries = new ListTag();
            for (Map.Entry<GlobalPos, Integer> signal : entry.getValue().entrySet()) {
                CompoundTag cmp = new CompoundTag();
                cmp.putString("L", signal.getKey().dimension().location().toString());
                cmp.putLong("P", signal.getKey().pos().asLong());
                cmp.putInt("R", signal.getValue());
                entries.add(cmp);
            }
            tag.put("V", entries);
            list.add(tag);
        }
        compound.put("Signals", list);
        return compound;
    }

    public int getStrength(UUID uid) {
        if (!this.signals.containsKey(uid)) {
            return 0;
        } else {
            return this.signals.get(uid).values().stream().max(Integer::compareTo).orElse(0);
        }
    }

    public void update(Level level, UUID uid, GlobalPos pos, int strength) {
        if (!this.signals.containsKey(uid)) {
            this.signals.put(uid, new HashMap<>());
            this.setDirty();
        }
        Map<GlobalPos, Integer> uidMap = this.signals.get(uid);
        if (!uidMap.containsKey(pos) || uidMap.get(pos) != strength) {
            uidMap.put(pos, strength);
            if (level instanceof ServerLevel) {
                for (GlobalPos targetPos : uidMap.keySet()) {
                    if (!pos.equals(targetPos)) {
                        ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(targetPos.dimension());
                        if (targetLevel != null) {
                            targetLevel.scheduleTick(targetPos.pos(), ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
                        }
                    }
                }
            }
            this.setDirty();
        }
    }

    public void remove(Level level, @Nullable UUID uid, GlobalPos pos) {
        if (uid != null) {
            if (this.signals.containsKey(uid)) {
                if (this.signals.get(uid).remove(pos) != null) {
                    if (level instanceof ServerLevel) {
                        for (GlobalPos targetPos : this.signals.get(uid).keySet()) {
                            if (!pos.equals(targetPos)) {
                                ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(targetPos.dimension());
                                if (targetLevel != null) {
                                    targetLevel.scheduleTick(targetPos.pos(), ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
                                }
                            }
                        }
                    }
                    this.setDirty();
                }
                if (this.signals.get(uid).isEmpty()) {
                    this.signals.remove(uid);
                    this.setDirty();
                }
            }
        } else {
            this.signals.keySet().forEach(x -> this.remove(level, x, pos));
        }
    }
}
