package de.melanx.utilitix.wireless;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModBlocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WirelessStorage extends WorldSavedData {

    public static final String ID = UtilitiX.getInstance().modid + "_wireless";

    public static WirelessStorage get(World world) {
        if (!world.isRemote) {
            DimensionSavedDataManager storage = ((ServerWorld) world).getServer().getOverworld().getSavedData();
            return storage.getOrCreate(WirelessStorage::new, ID);
        } else {
            return new WirelessStorage();
        }
    }

    private final Map<UUID, Map<WorldAndPos, Integer>> signals = new HashMap<>();

    public WirelessStorage() {
        this(ID);
    }

    public WirelessStorage(String name) {
        super(name);
    }

    @Override
    public void read(@Nonnull CompoundNBT nbt) {
        this.signals.clear();
        ListNBT list = nbt.getList("Signals", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            UUID uid = tag.getUniqueId("K");
            ListNBT entries = tag.getList("V", Constants.NBT.TAG_COMPOUND);
            Map<WorldAndPos, Integer> signalMap = new HashMap<>();
            for (int j = 0; j < entries.size(); j++) {
                WorldAndPos pos = WorldAndPos.deserialize(tag);
                if (pos != null) {
                    int strength = tag.getInt("R");
                    signalMap.put(pos, strength);
                }
            }
            this.signals.put(uid, signalMap);
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        for (Map.Entry<UUID, Map<WorldAndPos, Integer>> entry : this.signals.entrySet()) {
            CompoundNBT tag = new CompoundNBT();
            tag.putUniqueId("K", entry.getKey());
            ListNBT entries = new ListNBT();
            for (Map.Entry<WorldAndPos, Integer> signal : entry.getValue().entrySet()) {
                CompoundNBT cmp = signal.getKey().serialize();
                cmp.putInt("R", signal.getValue());
                entries.add(cmp);
            }
            tag.put("V", entries);
            list.add(tag);
        }
        nbt.put("Signals", list);
        return nbt;
    }

    public int getStrength(UUID uid) {
        if (!this.signals.containsKey(uid)) {
            return 0;
        } else {
            return this.signals.get(uid).values().stream().max(Integer::compareTo).orElse(0);
        }
    }

    public void update(World world, UUID uid, WorldAndPos pos, int strength) {
        if (!this.signals.containsKey(uid)) {
            this.signals.put(uid, new HashMap<>());
            this.markDirty();
        }
        Map<WorldAndPos, Integer> uidMap = this.signals.get(uid);
        if (!uidMap.containsKey(pos) || uidMap.get(pos) != strength) {
            uidMap.put(pos, strength);
            if (world instanceof ServerWorld) {
                for (WorldAndPos targetPos : uidMap.keySet()) {
                    if (!pos.equals(targetPos)) {
                        ServerWorld targetWorld = ((ServerWorld) world).getServer().getWorld(targetPos.dimension);
                        if (targetWorld != null) {
                            targetWorld.getPendingBlockTicks().scheduleTick(targetPos.pos, ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
                        }
                    }
                }
            }
            this.markDirty();
        }
    }

    public void remove(World world, @Nullable UUID uid, WorldAndPos pos) {
        if (uid != null) {
            if (this.signals.containsKey(uid)) {
                if (this.signals.get(uid).remove(pos) != null) {
                    if (world instanceof ServerWorld) {
                        for (WorldAndPos targetPos : this.signals.get(uid).keySet()) {
                            if (!pos.equals(targetPos)) {
                                ServerWorld targetWorld = ((ServerWorld) world).getServer().getWorld(targetPos.dimension);
                                if (targetWorld != null) {
                                    targetWorld.getPendingBlockTicks().scheduleTick(targetPos.pos, ModBlocks.linkedRepeater, 1, TickPriority.HIGH);
                                }
                            }
                        }
                    }
                    this.markDirty();
                }
                if (this.signals.get(uid).isEmpty()) {
                    this.signals.remove(uid);
                    this.markDirty();
                }
            }
        } else {
            this.signals.keySet().forEach(x -> this.remove(world, x, pos));
        }
    }
}