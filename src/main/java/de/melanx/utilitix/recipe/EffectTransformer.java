package de.melanx.utilitix.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.util.Misc;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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

    public abstract void write(PacketBuffer buffer);

    public static EffectTransformer deserialize(JsonObject json) {
        String type = json.get("type").getAsString();
        if ("apply".equalsIgnoreCase(type)) {
            ITextComponent name = json.has("name") ? ITextComponent.Serializer.getComponentFromJson(json.get("name")) : null;
            ImmutableList.Builder<EffectInstance> effects = ImmutableList.builder();
            JsonArray list = json.get("effects").getAsJsonArray();
            for (int i = 0; i < list.size(); i++) {
                effects.add(deserializeEffect(list.get(i).getAsJsonObject()));
            }
            return new Apply(name, effects.build());
        } else if ("merge".equalsIgnoreCase(type)) {
            return new Merge(json.get("fail_multiplier").getAsFloat());
        } else if ("upgrade".equalsIgnoreCase(type)) {
            return new Upgrade(json.get("max_level").getAsInt());
        } else if ("clone".equalsIgnoreCase(type)) {
            return new Clone();
        } else {
            throw new IllegalStateException("Invalid json: Unknown effect transformer type: " + type);
        }
    }

    public static EffectTransformer read(PacketBuffer buffer) {
        byte id = buffer.readByte();
        if (id == 0) {
            ITextComponent name = null;
            if (buffer.readBoolean()) {
                name = buffer.readTextComponent();
            }
            ImmutableList.Builder<EffectInstance> effects = ImmutableList.builder();
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                CompoundNBT nbt = buffer.readCompoundTag();
                if (nbt != null) {
                    effects.add(EffectInstance.read(nbt));
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

    public static ItemStack create(Item item, List<EffectInstance> effects) {
        ItemStack stack = new ItemStack(item);
        PotionUtils.appendEffects(stack, effects);
        stack.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getPotionColorFromEffectList(effects));
        return stack;
    }

    public static JsonObject serializeEffect(EffectInstance effect) {
        JsonObject json = new JsonObject();
        ResourceLocation id = effect.getPotion().getRegistryName();
        if (id == null) id = Misc.MISSIGNO;
        json.addProperty("effect", id.toString());
        json.addProperty("amplifier", effect.getAmplifier() + 1);
        json.addProperty("duration", effect.getDuration());
        if (effect.isAmbient()) {
            json.addProperty("ambient", effect.isAmbient());
        }
        if (!effect.doesShowParticles()) {
            json.addProperty("particles", effect.doesShowParticles());
        }
        return json;
    }

    public static EffectInstance deserializeEffect(JsonObject json) {
        Effect potion = ForgeRegistries.POTIONS.getValue(ResourceLocation.tryCreate(json.get("effect").getAsString()));
        int amplifier = Math.max(0, json.get("amplifier").getAsInt() - 1);
        int duration = Math.max(1, json.get("duration").getAsInt());
        boolean ambient = json.has("ambient") && json.get("ambient").getAsBoolean();
        boolean particles = !json.has("particles") || json.get("particles").getAsBoolean();
        return new EffectInstance(potion == null ? Effects.SPEED : potion, duration, amplifier, ambient, particles);
    }

    public static class Apply extends EffectTransformer {

        @Nullable
        private final ITextComponent name;
        private final List<EffectInstance> effects;

        public Apply(EffectInstance... effects) {
            this(null, effects);
        }

        public Apply(List<EffectInstance> effects) {
            this(null, effects);
        }

        public Apply(@Nullable ITextComponent name, EffectInstance... effects) {
            this(name, ImmutableList.copyOf(effects));
        }

        public Apply(@Nullable ITextComponent name, List<EffectInstance> effects) {
            this.name = name;
            this.effects = ImmutableList.copyOf(effects);
        }

        @Override
        public boolean canTransform(PotionInput input) {
            return ModItemTags.POTIONS.contains(input.getMain().getItem()) && input.testEffectsMain(List::isEmpty);
        }

        @Override
        public ItemStack output() {
            ItemStack stack = create(Items.POTION, this.effects);
            if (this.name != null) {
                stack.setDisplayName(this.name.deepCopy());
            }
            return stack;
        }

        @Nullable
        @Override
        public PotionOutput transform(PotionInput input) {
            ItemStack stack = create(input.getMain().getItem(), this.effects);
            if (this.name != null) {
                stack.setDisplayName(this.name.deepCopy());
            }
            return PotionOutput.simple(stack);
        }

        @Override
        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("type", "apply");
            if (this.name != null) {
                json.add("name", ITextComponent.Serializer.toJsonTree(this.name));
            }
            JsonArray list = new JsonArray();
            for (EffectInstance effect : this.effects) {
                list.add(serializeEffect(effect));
            }
            json.add("effects", list);
            return json;
        }

        @Override
        public void write(PacketBuffer buffer) {
            buffer.writeByte(0);
            buffer.writeBoolean(this.name != null);
            if (this.name != null) {
                buffer.writeTextComponent(this.name);
            }
            buffer.writeVarInt(this.effects.size());
            for (EffectInstance effect : this.effects) {
                buffer.writeCompoundTag(effect.write(new CompoundNBT()));
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
            List<EffectInstance> merged = new ArrayList<>();
            if (input.getEffects1() != null) {
                for (EffectInstance effect : input.getEffects1()) {
                    this.addMergedEffectToList(effect.getPotion(), merged, input.getEffects1(), input.getEffects2());
                }
            }
            if (input.getEffects2() != null) {
                for (EffectInstance effect : input.getEffects2()) {
                    this.addMergedEffectToList(effect.getPotion(), merged, input.getEffects1(), input.getEffects2());
                }
            }
            float chance = Math.max(0, merged.size() + 1) * this.failMultiplier;
            if (new Random().nextInt(100) < chance) {
                return PotionOutput.simple(new ItemStack(ModItems.failedPotion));
            } else {
                ItemStack stack = create(input.getIn1().getItem(), merged);
                stack.setDisplayName(new TranslationTextComponent("item.utilitix.merged_potion").mergeStyle(TextFormatting.GREEN));
                return PotionOutput.simple(stack);
            }
        }

        private void addMergedEffectToList(Effect potion, List<EffectInstance> mergeList, @Nullable List<EffectInstance> list1, @Nullable List<EffectInstance> list2) {
            for (EffectInstance effect : mergeList) {
                if (effect.getPotion() == potion)
                    return;
            }

            EffectInstance effect1 = null;
            EffectInstance effect2 = null;
            if (list1 != null) {
                for (EffectInstance effect : list1) {
                    if (effect.getPotion() == potion) {
                        effect1 = effect;
                        break;
                    }
                }
            }
            if (list2 != null) {
                for (EffectInstance effect : list2) {
                    if (effect.getPotion() == potion) {
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
        public void write(PacketBuffer buffer) {
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
                EffectInstance old = input.getEffectsMain().get(0);
                return PotionOutput.simple(create(input.getMain().getItem(), ImmutableList.of(new EffectInstance(old.getPotion(), old.getDuration(), MathHelper.clamp(old.getAmplifier() + 1, 0, this.maxLevel), old.isAmbient(), old.doesShowParticles()))));
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
        public void write(PacketBuffer buffer) {
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
        public void write(PacketBuffer buffer) {
            buffer.writeByte(3);
        }
    }
}
