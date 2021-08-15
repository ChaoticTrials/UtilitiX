package de.melanx.utilitix.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.util.Misc;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class EffectTransformer {

    private EffectTransformer() {

    }

    public abstract boolean canTransform(PotionInput input);

    public abstract ItemStack output();

    @Nullable
    public abstract PotionOutput transform(PotionInput input);

    public abstract JsonObject serialize();

    public abstract void write(FriendlyByteBuf buffer);

    public static EffectTransformer deserialize(JsonObject json) {
        String type = json.get("type").getAsString();
        if ("apply".equalsIgnoreCase(type)) {
            Component name = json.has("name") ? Component.Serializer.fromJson(json.get("name")) : null;
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
                name = buffer.readComponent();
            }
            ImmutableList.Builder<MobEffectInstance> effects = ImmutableList.builder();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                CompoundTag nbt = buffer.readNbt();
                if (nbt != null) {
                    effects.add(MobEffectInstance.load(nbt));
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
        PotionUtils.setCustomEffects(stack, effects);
        stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(effects));
        return stack;
    }

    public static JsonObject serializeEffect(MobEffectInstance effect) {
        JsonObject json = new JsonObject();
        ResourceLocation id = effect.getEffect().getRegistryName();
        if (id == null) id = Misc.MISSIGNO;
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
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(ResourceLocation.tryParse(json.get("effect").getAsString()));
        int amplifier = Math.max(0, json.get("amplifier").getAsInt() - 1);
        int duration = Math.max(1, json.get("duration").getAsInt());
        boolean ambient = json.has("ambient") && json.get("ambient").getAsBoolean();
        boolean particles = !json.has("particles") || json.get("particles").getAsBoolean();
        return new MobEffectInstance(effect == null ? MobEffects.MOVEMENT_SPEED : effect, duration, amplifier, ambient, particles);
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
            return ModItemTags.POTIONS.contains(input.getMain().getItem()) && input.testEffectsMain(List::isEmpty) && input.getIn1().isEmpty() && input.getIn2().isEmpty();
        }

        @Override
        public ItemStack output() {
            ItemStack stack = create(Items.POTION, this.effects);
            if (this.name != null) {
                stack.setHoverName(this.name.copy());
            }
            return stack;
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            ItemStack stack = create(input.getMain().getItem(), this.effects);
            if (this.name != null) {
                stack.setHoverName(this.name.copy());
            }
            return PotionOutput.simple(stack);
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "apply");
            if (this.name != null) {
                json.add("name", Component.Serializer.toJsonTree(this.name));
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
                buffer.writeComponent(this.name);
            }
            buffer.writeVarInt(this.effects.size());
            for (MobEffectInstance effect : this.effects) {
                buffer.writeNbt(effect.save(new CompoundTag()));
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
                    && input.testEffects1(list -> !list.isEmpty()) && input.testEffects2(list -> !list.isEmpty());
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
                for (MobEffectInstance effect : input.getEffects1()) {
                    this.addMergedEffectToList(effect.getEffect(), merged, input.getEffects1(), input.getEffects2());
                }
            }
            if (input.getEffects2() != null) {
                for (MobEffectInstance effect : input.getEffects2()) {
                    this.addMergedEffectToList(effect.getEffect(), merged, input.getEffects1(), input.getEffects2());
                }
            }
            float chance = Math.max(0, merged.size() + 1) * this.failMultiplier;
            if (new Random().nextInt(100) < chance) {
                return PotionOutput.simple(new ItemStack(ModItems.failedPotion));
            } else {
                ItemStack stack = create(input.getIn1().getItem(), merged);
                stack.setHoverName(new TranslatableComponent("item.utilitix.merged_potion").withStyle(ChatFormatting.GREEN));
                return PotionOutput.simple(stack);
            }
        }

        private void addMergedEffectToList(MobEffect potion, List<MobEffectInstance> mergeList, @Nullable List<MobEffectInstance> list1, @Nullable List<MobEffectInstance> list2) {
            for (MobEffectInstance effect : mergeList) {
                if (effect.getEffect() == potion)
                    return;
            }

            MobEffectInstance effect1 = null;
            MobEffectInstance effect2 = null;
            if (list1 != null) {
                for (MobEffectInstance effect : list1) {
                    if (effect.getEffect() == potion) {
                        effect1 = effect;
                        break;
                    }
                }
            }
            if (list2 != null) {
                for (MobEffectInstance effect : list2) {
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
            return input.testEffectsMain(list -> list.size() == 1 && list.get(0).getAmplifier() < this.maxLevel)
                    && input.getIn1().isEmpty() && input.getIn2().isEmpty();
        }

        @Override
        public ItemStack output() {
            return new ItemStack(Items.POTION);
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            if (input.getEffectsMain() == null || input.getEffectsMain().isEmpty()) {
                return null;
            } else {
                MobEffectInstance old = input.getEffectsMain().get(0);
                ItemStack newStack = create(input.getMain().getItem(), ImmutableList.of(new MobEffectInstance(old.getEffect(), old.getDuration(), Mth.clamp(old.getAmplifier() + 1, 0, this.maxLevel), old.isAmbient(), old.isVisible())));
                newStack.setHoverName(input.getMain().getHoverName());
                return PotionOutput.simple(newStack);
            }
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
            return input.testEffectsMain(list -> !list.isEmpty()) && ModItemTags.POTIONS.contains(input.getIn1().getItem())
                    && input.getEffects1() == null && ModItemTags.POTIONS.contains(input.getIn2().getItem())
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
