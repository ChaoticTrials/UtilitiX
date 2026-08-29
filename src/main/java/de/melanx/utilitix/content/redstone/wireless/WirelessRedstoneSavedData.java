package de.melanx.utilitix.content.redstone.wireless;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WirelessRedstoneSavedData extends SavedData {

    public static final SavedDataType<WirelessRedstoneSavedData> TYPE = new SavedDataType<>(
            UtilitiX.getInstance().id("wireless"),
            WirelessRedstoneSavedData::new,
            WirelessRedstoneSavedData.UidEntry.CODEC.listOf().xmap(WirelessRedstoneSavedData::fromEntries, WirelessRedstoneSavedData::toEntries)
                    .xmap(WirelessRedstoneSavedData::new, data -> data.signals)
    );

    private static Map<UUID, Map<GlobalPos, Integer>> fromEntries(List<UidEntry> list) {
        Map<UUID, Map<GlobalPos, Integer>> signals = new HashMap<>();
        for (UidEntry entry : list) {
            Map<GlobalPos, Integer> signalMap = new HashMap<>();
            for (SignalEntry signal : entry.signals()) {
                signalMap.put(signal.pos(), signal.strength());
            }

            signals.put(entry.uid(), signalMap);
        }

        return signals;
    }

    private static List<UidEntry> toEntries(Map<UUID, Map<GlobalPos, Integer>> signals) {
        List<UidEntry> list = new java.util.ArrayList<>();
        for (Map.Entry<UUID, Map<GlobalPos, Integer>> entry : signals.entrySet()) {
            List<SignalEntry> signalList = new java.util.ArrayList<>();
            for (Map.Entry<GlobalPos, Integer> signal : entry.getValue().entrySet()) {
                signalList.add(new SignalEntry(signal.getKey(), signal.getValue()));
            }

            list.add(new UidEntry(entry.getKey(), signalList));
        }

        return list;
    }

    public static WirelessRedstoneSavedData get(Level level) {
        if (!(level instanceof ServerLevel)) {
            return new WirelessRedstoneSavedData();
        }

        SavedDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(TYPE);
    }

    private final Map<UUID, Map<GlobalPos, Integer>> signals;

    public WirelessRedstoneSavedData() {
        this(new HashMap<>());
    }

    public WirelessRedstoneSavedData(Map<UUID, Map<GlobalPos, Integer>> signals) {
        this.signals = signals;
    }

    public int getStrength(UUID uid) {
        if (!this.signals.containsKey(uid)) {
            return 0;
        }

        return this.signals.get(uid).values().stream().max(Integer::compareTo).orElse(0);
    }

    public void update(Level level, UUID uid, GlobalPos pos, int strength) {
        if (!this.signals.containsKey(uid)) {
            this.signals.put(uid, new HashMap<>());
            this.setDirty();
        }

        Map<GlobalPos, Integer> uidMap = this.signals.get(uid);
        if (uidMap.containsKey(pos) && uidMap.get(pos) == strength) {
            return;
        }

        uidMap.put(pos, strength);
        if (level instanceof ServerLevel serverLevel) {
            for (GlobalPos targetPos : uidMap.keySet()) {
                if (!pos.equals(targetPos)) {
                    ServerLevel targetLevel = serverLevel.getServer().getLevel(targetPos.dimension());

                    if (targetLevel != null) {
                        targetLevel.scheduleTick(targetPos.pos(), ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
                    }
                }
            }
        }

        this.setDirty();
    }

    public void remove(Level level, @Nullable UUID uid, GlobalPos pos) {
        if (uid == null) {
            this.signals.keySet().forEach(x -> this.remove(level, x, pos));
            return;
        }

        if (!this.signals.containsKey(uid)) {
            return;
        }

        if (this.signals.get(uid).remove(pos) != null) {
            if (level instanceof ServerLevel serverLevel) {
                for (GlobalPos targetPos : this.signals.get(uid).keySet()) {
                    if (pos.equals(targetPos)) {
                        continue;
                    }

                    ServerLevel targetLevel = serverLevel.getServer().getLevel(targetPos.dimension());

                    if (targetLevel != null) {
                        targetLevel.scheduleTick(targetPos.pos(), ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
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

    private record SignalEntry(GlobalPos pos, int strength) {

        private static final Codec<SignalEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                GlobalPos.CODEC.fieldOf("pos").forGetter(SignalEntry::pos),
                Codec.INT.fieldOf("strength").forGetter(SignalEntry::strength)
        ).apply(i, SignalEntry::new));
    }

    private record UidEntry(UUID uid, List<SignalEntry> signals) {

        private static final Codec<UidEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
                UUIDUtil.CODEC.fieldOf("uid").forGetter(UidEntry::uid),
                SignalEntry.CODEC.listOf().fieldOf("signals").forGetter(UidEntry::signals)
        ).apply(i, UidEntry::new));
    }
}
