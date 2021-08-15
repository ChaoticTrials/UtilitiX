package de.melanx.utilitix.content.wireless;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
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

    private final Map<UUID, Map<WorldAndPos, Integer>> signals = new HashMap<>();

    @Nonnull
    public WirelessStorage load(@Nonnull CompoundTag nbt) {
        this.signals.clear();
        ListTag list = nbt.getList("Signals", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            UUID uid = tag.getUUID("K");
            ListTag entries = tag.getList("V", Constants.NBT.TAG_COMPOUND);
            Map<WorldAndPos, Integer> signalMap = new HashMap<>();
            for (int j = 0; j < entries.size(); j++) {
                CompoundTag cmp = entries.getCompound(j);
                WorldAndPos pos = WorldAndPos.deserialize(cmp);
                if (pos != null) {
                    int strength = cmp.getInt("R");
                    signalMap.put(pos, strength);
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
        for (Map.Entry<UUID, Map<WorldAndPos, Integer>> entry : this.signals.entrySet()) {
            CompoundTag tag = new CompoundTag();
            tag.putUUID("K", entry.getKey());
            ListTag entries = new ListTag();
            for (Map.Entry<WorldAndPos, Integer> signal : entry.getValue().entrySet()) {
                CompoundTag cmp = signal.getKey().serialize();
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

    public void update(Level level, UUID uid, WorldAndPos pos, int strength) {
        if (!this.signals.containsKey(uid)) {
            this.signals.put(uid, new HashMap<>());
            this.setDirty();
        }
        Map<WorldAndPos, Integer> uidMap = this.signals.get(uid);
        if (!uidMap.containsKey(pos) || uidMap.get(pos) != strength) {
            uidMap.put(pos, strength);
            if (level instanceof ServerLevel) {
                for (WorldAndPos targetPos : uidMap.keySet()) {
                    if (!pos.equals(targetPos)) {
                        ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(targetPos.dimension);
                        if (targetLevel != null) {
                            targetLevel.getBlockTicks().scheduleTick(targetPos.pos, ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
                        }
                    }
                }
            }
            this.setDirty();
        }
    }

    public void remove(Level level, @Nullable UUID uid, WorldAndPos pos) {
        if (uid != null) {
            if (this.signals.containsKey(uid)) {
                if (this.signals.get(uid).remove(pos) != null) {
                    if (level instanceof ServerLevel) {
                        for (WorldAndPos targetPos : this.signals.get(uid).keySet()) {
                            if (!pos.equals(targetPos)) {
                                ServerLevel targetLevel = ((ServerLevel) level).getServer().getLevel(targetPos.dimension);
                                if (targetLevel != null) {
                                    targetLevel.getBlockTicks().scheduleTick(targetPos.pos, ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
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
