package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BaseCart extends AbstractMinecart {

    private static final Map<EntityType<?>, Item> CART_ITEMS = Collections.synchronizedMap(new HashMap<>());

    protected BaseCart(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Nonnull
    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    @Nonnull
    @Override
    public Item getDropItem() {
        return CART_ITEMS.getOrDefault(this.getType(), Items.MINECART);
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    public static <T extends BaseCart> CartType<T> type(String id, EntityType.EntityFactory<T> factory) {
        return BaseCart.type(id, factory, new Item.Properties().stacksTo(1));
    }

    public static <T extends BaseCart> CartType<T> type(String id, EntityType.EntityFactory<T> factory, Item.Properties properties) {
        EntityType<T> type = EntityType.Builder.of(factory, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(UtilitiX.getInstance().modid + "_" + id);
        ItemBase item = new BaseCartItem(UtilitiX.getInstance(), type, properties);

        CART_ITEMS.put(type, item);

        return new DefaultCartType<>(type, item);
    }

    public static double getHorizontalDistanceSqr(Vec3 vec) {
        return vec.x * vec.x + vec.z * vec.z;
    }

    public interface CartType<T extends BaseCart> extends Registerable {

        EntityType<T> get();

        ItemBase item();
    }

    private static class DefaultCartType<T extends BaseCart> implements CartType<T> {

        private final EntityType<T> type;
        private final ItemBase item;

        private DefaultCartType(EntityType<T> type, ItemBase item) {
            this.type = type;
            this.item = item;
        }

        @Override
        public EntityType<T> get() {
            return this.type;
        }

        @Override
        public ItemBase item() {
            return this.item;
        }

        @Override
        public void registerAdditional(RegistrationContext ctx, EntryCollector builder) {
            builder.register(Registries.ENTITY_TYPE, this.type);
            builder.register(Registries.ITEM, this.item);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void setupClient(SetupContext ctx) {
            ctx.enqueue(() -> EntityRenderers.register(this.type, context -> new MinecartRendererX<>(context, ModelLayers.MINECART)));
        }
    }
}
