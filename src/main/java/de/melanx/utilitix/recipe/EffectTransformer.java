package de.melanx.utilitix.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.moddingx.libx.util.Misc;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public abstract class EffectTransformer {

    public static final MapCodec<EffectTransformer> CODEC =
            Codec.STRING.dispatchMap("type", EffectTransformer::typeId, EffectTransformer::codecForType);

    public static final Codec<EffectTransformer> DIRECT_CODEC = CODEC.codec();

    /**
     * Holder<MobEffect> codec without relying on holderByNameCodec() existing in your mappings
     */
    public static final Codec<Holder<MobEffect>> MOB_EFFECT_HOLDER_CODEC =
            ResourceLocation.CODEC.comapFlatMap(
                    id -> BuiltInRegistries.MOB_EFFECT.getHolder(id)
                            .<DataResult<Holder<MobEffect>>>map(DataResult::success)
                            .orElseGet(() -> DataResult.error(() -> "Unknown mob effect: " + id)),
                    holder -> {
                        ResourceLocation key = BuiltInRegistries.MOB_EFFECT.getKey(holder.value());
                        return key == null ? Misc.MISSINGNO : key;
                    }
            );

    /**
     * Matches your JSON schema for effects:
     * {
     * "effect": "minecraft:speed",
     * "amplifier": 1,      // 1-based in json
     * "duration": 200,
     * "ambient": false,    // optional
     * "particles": true    // optional
     * }
     */
    public static final Codec<MobEffectInstance> EFFECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MOB_EFFECT_HOLDER_CODEC.fieldOf("effect")
                    .forGetter(MobEffectInstance::getEffect),

            Codec.intRange(1, 255).optionalFieldOf("amplifier", 1)
                    .forGetter(e -> e.getAmplifier() + 1),

            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("duration")
                    .forGetter(MobEffectInstance::getDuration),

            Codec.BOOL.optionalFieldOf("ambient", false)
                    .forGetter(MobEffectInstance::isAmbient),

            Codec.BOOL.optionalFieldOf("particles", true)
                    .forGetter(MobEffectInstance::isVisible)
    ).apply(instance, (effect, ampPlusOne, duration, ambient, particles) ->
            new MobEffectInstance(effect, duration, Math.max(0, ampPlusOne - 1), ambient, particles)
    ));

    private static final MapCodec<Apply> APPLY_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            // If ComponentSerialization.CODEC doesn't exist in your MC version, replace with the correct component codec for JSON in your mappings.
            ComponentSerialization.CODEC.optionalFieldOf("name")
                    .forGetter(a -> Optional.ofNullable(a.name)),

            EFFECT_CODEC.listOf().fieldOf("effects")
                    .forGetter(a -> a.effects)
    ).apply(instance, (nameOpt, effects) -> new Apply(nameOpt.orElse(null), effects)));

    private static final MapCodec<Merge> MERGE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("fail_multiplier")
                    .forGetter(m -> m.failMultiplier)
    ).apply(instance, Merge::new));

    private static final MapCodec<Upgrade> UPGRADE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 255).fieldOf("max_level")
                    .forGetter(u -> u.maxLevel + 1)
    ).apply(instance, maxLevelOneBased -> new Upgrade(Math.max(0, maxLevelOneBased - 1))));

    private static final MapCodec<Clone> CLONE_CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<Clone> decode(DynamicOps<T> ops, MapLike<T> input) {
            return DataResult.success(new Clone());
        }

        @Override
        public <T> RecordBuilder<T> encode(Clone input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return prefix; // no fields
        }
    };

    private static String typeId(EffectTransformer t) {
        if (t instanceof Apply) return "apply";
        if (t instanceof Merge) return "merge";
        if (t instanceof Upgrade) return "upgrade";
        if (t instanceof Clone) return "clone";
        throw new IllegalStateException("Unknown EffectTransformer: " + t.getClass().getName());
    }

    private static MapCodec<? extends EffectTransformer> codecForType(String type) {
        return switch(type.toLowerCase(Locale.ROOT)) {
            case "apply" -> APPLY_CODEC;
            case "merge" -> MERGE_CODEC;
            case "upgrade" -> UPGRADE_CODEC;
            case "clone" -> CLONE_CODEC;
            default -> errorMapCodec("Unknown effect transformer type: " + type);
        };
    }

    private static <T> MapCodec<T> errorMapCodec(String message) {
        return new MapCodec<>() {
            @Override
            public <U> Stream<U> keys(DynamicOps<U> ops) {
                return Stream.empty();
            }

            @Override
            public <U> DataResult<T> decode(DynamicOps<U> ops, MapLike<U> input) {
                return DataResult.error(() -> message);
            }

            @Override
            public <U> RecordBuilder<U> encode(T input, DynamicOps<U> ops, RecordBuilder<U> prefix) {
                return prefix.withErrorsFrom(DataResult.error(() -> message));
            }

            @Override
            public String toString() {
                return "ErrorMapCodec[" + message + "]";
            }
        };
    }

    private EffectTransformer() {}

    public abstract boolean canTransform(PotionInput input);

    public abstract ItemStack output();

    @Nullable
    public abstract PotionOutput transform(PotionInput input);

    public abstract JsonObject serialize();

    public abstract void write(FriendlyByteBuf buffer);

    public static EffectTransformer deserialize(JsonObject json) {
        String type = json.get("type").getAsString();
        if ("apply".equalsIgnoreCase(type)) {
            Component name = Component.literal("todo"); // todo json.has("name") ? Component.Serializer.fromJson(json.get("name")) : null;
            ImmutableList.Builder<MobEffectInstance> effects = ImmutableList.builder();
            JsonArray list = json.get("effects").getAsJsonArray();
            for (int i = 0; i < list.size(); i++) {
                effects.add(deserializeEffect(list.get(i).getAsJsonObject()));
            }
            return new Apply(name, effects.build());
        } else if ("merge".equalsIgnoreCase(type)) {
            return new Merge(json.get("fail_multiplier").getAsFloat());
        } else if ("upgrade".equalsIgnoreCase(type)) {
            return new Upgrade(Math.max(0, json.get("max_level").getAsInt() - 1));
        } else if ("clone".equalsIgnoreCase(type)) {
            return new Clone();
        } else {
            throw new IllegalStateException("Invalid json: Unknown effect transformer type: " + type);
        }
    }

    public static EffectTransformer read(FriendlyByteBuf buffer) {
        byte id = buffer.readByte();
        if (id == 0) {
            Component name = null;
            if (buffer.readBoolean()) {
//                name = buffer.readComponent(); todo
            }
            ImmutableList.Builder<MobEffectInstance> effects = ImmutableList.builder();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                CompoundTag nbt = buffer.readNbt();
                if (nbt != null) {
                    effects.add(Objects.requireNonNull(MobEffectInstance.load(nbt)));
                }
            }
            return new Apply(name, effects.build());
        } else if (id == 1) {
            return new Merge(buffer.readFloat());
        } else if (id == 2) {
            return new Upgrade(buffer.readVarInt());
        } else if (id == 3) {
            return new Clone();
        } else {
            throw new IllegalStateException("Invalid packet: Unknown effect transformer type: " + id);
        }
    }

    public static ItemStack create(Item item, List<MobEffectInstance> effects) {
        ItemStack stack = new ItemStack(item);
//        PotionUtils.setCustomEffects(stack, effects); todo
//        stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(effects));
        return stack;
    }

    public static JsonObject serializeEffect(MobEffectInstance effect) {
        JsonObject json = new JsonObject();
        ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value());
        if (id == null) id = Misc.MISSINGNO;
        json.addProperty("effect", id.toString());
        json.addProperty("amplifier", effect.getAmplifier() + 1);
        json.addProperty("duration", effect.getDuration());
        if (effect.isAmbient()) {
            json.addProperty("ambient", effect.isAmbient());
        }
        if (!effect.isVisible()) {
            json.addProperty("particles", effect.isVisible());
        }
        return json;
    }

    public static MobEffectInstance deserializeEffect(JsonObject json) {
        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.getHolder(Objects.requireNonNull(ResourceLocation.tryParse(json.get("effect").getAsString()))).orElse(BuiltInRegistries.MOB_EFFECT.getHolderOrThrow(Objects.requireNonNull(MobEffects.MOVEMENT_SPEED.getKey())));
        int amplifier = Math.max(0, json.get("amplifier").getAsInt() - 1);
        int duration = Math.max(1, json.get("duration").getAsInt());
        boolean ambient = json.has("ambient") && json.get("ambient").getAsBoolean();
        boolean particles = !json.has("particles") || json.get("particles").getAsBoolean();

        return new MobEffectInstance(effect, duration, amplifier, ambient, particles);
    }

    public static class Apply extends EffectTransformer {

        @Nullable
        private final Component name;
        private final List<MobEffectInstance> effects;

        public Apply(MobEffectInstance... effects) {
            this(null, effects);
        }

        public Apply(List<MobEffectInstance> effects) {
            this(null, effects);
        }

        public Apply(@Nullable Component name, MobEffectInstance... effects) {
            this(name, ImmutableList.copyOf(effects));
        }

        public Apply(@Nullable Component name, List<MobEffectInstance> effects) {
            this.name = name;
            this.effects = ImmutableList.copyOf(effects);
        }

        @Override
        public boolean canTransform(PotionInput input) {
            return input.getMain().is(ModItemTags.POTIONS) && input.testEffectsMain(contents -> contents.customEffects().isEmpty()) && input.getIn1().isEmpty() && input.getIn2().isEmpty();
        }

        @Override
        public ItemStack output() {
            ItemStack stack = EffectTransformer.create(Items.POTION, this.effects);
            if (this.name != null) {
                stack.set(DataComponents.CUSTOM_NAME, this.name.copy());
            }

            return stack;
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            ItemStack stack = EffectTransformer.create(input.getMain().getItem(), this.effects);
            if (this.name != null) {
                stack.set(DataComponents.CUSTOM_NAME, this.name.copy());
            }

            return PotionOutput.simple(stack);
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "apply");
            if (this.name != null) {
//                json.add("name", Component.Serializer.toJsonTree(this.name)); todo
            }
            JsonArray list = new JsonArray();
            for (MobEffectInstance effect : this.effects) {
                list.add(serializeEffect(effect));
            }
            json.add("effects", list);
            return json;
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(0);
            buffer.writeBoolean(this.name != null);
            if (this.name != null) {
                ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.encode(buffer, this.name);
            }
            buffer.writeVarInt(this.effects.size());
            for (MobEffectInstance effect : this.effects) {
                buffer.writeNbt(effect.save());
            }
        }
    }

    public static class Merge extends EffectTransformer {

        private final float failMultiplier;

        public Merge(float failMultiplier) {
            this.failMultiplier = failMultiplier;
        }

        @Override
        public boolean canTransform(PotionInput input) {
            return input.getMain().getItem() == Items.GLASS_BOTTLE && input.getIn1().getItem() == input.getIn2().getItem()
                    && input.testEffects1(list -> !list.customEffects().isEmpty()) && input.testEffects2(list -> !list.customEffects().isEmpty());
        }

        @Override
        public ItemStack output() {
            return new ItemStack(Items.POTION);
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            List<MobEffectInstance> merged = new ArrayList<>();
            if (input.getEffects1() != null) {
                for (MobEffectInstance effect : input.getEffects1().customEffects()) {
                    this.addMergedEffectToList(effect.getEffect(), merged, input.getEffects1(), input.getEffects2());
                }
            }
            if (input.getEffects2() != null) {
                for (MobEffectInstance effect : input.getEffects2().customEffects()) {
                    this.addMergedEffectToList(effect.getEffect(), merged, input.getEffects1(), input.getEffects2());
                }
            }
            float chance = Math.max(0, merged.size() + 1) * this.failMultiplier;
            if (new Random().nextInt(100) < chance) {
                return PotionOutput.simple(new ItemStack(ModItems.failedPotion));
            } else {
                ItemStack stack = EffectTransformer.create(input.getIn1().getItem(), merged);
                stack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.utilitix.merged_potion").withStyle(ChatFormatting.GREEN));
                return PotionOutput.simple(stack);
            }
        }

        private void addMergedEffectToList(Holder<MobEffect> potion, List<MobEffectInstance> mergeList, @Nullable PotionContents list1, @Nullable PotionContents list2) {
            for (MobEffectInstance effect : mergeList) {
                if (effect.getEffect() == potion)
                    return;
            }

            MobEffectInstance effect1 = null;
            MobEffectInstance effect2 = null;
            if (list1 != null) {
                for (MobEffectInstance effect : list1.customEffects()) {
                    if (effect.getEffect() == potion) {
                        effect1 = effect;
                        break;
                    }
                }
            }
            if (list2 != null) {
                for (MobEffectInstance effect : list2.customEffects()) {
                    if (effect.getEffect() == potion) {
                        effect2 = effect;
                        break;
                    }
                }
            }

            //noinspection StatementWithEmptyBody
            if (effect1 == null && effect2 == null) {
                //
            } else if (effect1 == null) {
                mergeList.add(effect2);
            } else if (effect2 == null) {
                mergeList.add(effect1);
            } else {
                boolean useFirst;
                if (effect1.getAmplifier() > effect2.getAmplifier())
                    useFirst = true;
                else if (effect2.getAmplifier() > effect1.getAmplifier())
                    useFirst = false;
                else
                    useFirst = effect1.getDuration() > effect2.getDuration();
                if (useFirst)
                    mergeList.add(effect1);
                else
                    mergeList.add(effect2);
            }
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "merge");
            json.addProperty("fail_multiplier", this.failMultiplier);
            return json;
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(1);
            buffer.writeFloat(this.failMultiplier);
        }
    }

    public static class Upgrade extends EffectTransformer {

        private final int maxLevel;

        public Upgrade(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        @Override
        public boolean canTransform(PotionInput input) {
            return input.testEffectsMain(list -> list.customEffects().size() == 1 && list.customEffects().getFirst().getAmplifier() < this.maxLevel)
                    && input.getIn1().isEmpty() && input.getIn2().isEmpty();
        }

        @Override
        public ItemStack output() {
            return new ItemStack(Items.POTION);
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            if (input.getEffectsMain() == null || input.getEffectsMain().customEffects().isEmpty()) {
                return null;
            }

            MobEffectInstance old = input.getEffectsMain().customEffects().getFirst();
            ItemStack newStack = EffectTransformer.create(input.getMain().getItem(), ImmutableList.of(new MobEffectInstance(old.getEffect(), old.getDuration(), Mth.clamp(old.getAmplifier() + 1, 0, this.maxLevel), old.isAmbient(), old.isVisible())));
            newStack.set(DataComponents.CUSTOM_NAME, input.getMain().getHoverName());

            return PotionOutput.simple(newStack);
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "upgrade");
            json.addProperty("max_level", this.maxLevel + 1);
            return json;
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(2);
            buffer.writeVarInt(this.maxLevel);
        }
    }

    public static class Clone extends EffectTransformer {

        @Override
        public boolean canTransform(PotionInput input) {
            return input.testEffectsMain(list -> !list.customEffects().isEmpty()) && input.getIn1().is(ModItemTags.POTIONS)
                    && input.getEffects1() == null && input.getIn2().is(ModItemTags.POTIONS)
                    && input.getEffects2() == null;
        }

        @Override
        public ItemStack output() {
            return new ItemStack(Items.POTION);
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            return PotionOutput.create(ItemStack.EMPTY, input.getMain().copy(), input.getMain().copy());
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "clone");
            return json;
        }

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeByte(3);
        }
    }
}
