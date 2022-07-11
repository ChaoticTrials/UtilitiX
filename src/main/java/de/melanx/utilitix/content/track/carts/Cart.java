package de.melanx.utilitix.content.track.carts;

import de.melanx.utilitix.UtilitiX;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.base.ItemBase;
import org.moddingx.libx.registration.Registerable;
import org.moddingx.libx.registration.RegistrationContext;
import org.moddingx.libx.registration.SetupContext;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Cart extends AbstractMinecart {

    private static final Map<EntityType<?>, Item> cartItems = Collections.synchronizedMap(new HashMap<>());

    protected Cart(EntityType<?> type, Level level) {
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
        return cartItems.getOrDefault(this.getType(), Items.MINECART);
    }

    @Override
    public boolean canBeRidden() {
        return false;
    }

    @Nonnull
    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static <T extends Cart> CartType<T> type(String id, EntityType.EntityFactory<T> factory) {
        return type(id, factory, new Item.Properties().stacksTo(1));
    }

    public static <T extends Cart> CartType<T> type(String id, EntityType.EntityFactory<T> factory, Item.Properties properties) {
        EntityType<T> type = EntityType.Builder.of(factory, MobCategory.MISC).sized(0.98F, 0.7F).clientTrackingRange(8).build(UtilitiX.getInstance().modid + "_" + id);
        ItemBase item = new ItemCart(UtilitiX.getInstance(), type, properties);
        cartItems.put(type, item);
        return new DefaultCartType<>(type, item);
    }

    public static double getHorizontalDistanceSqr(Vec3 vec) {
        return vec.x * vec.x + vec.z * vec.z;
    }

    public interface CartType<T extends Cart> extends Registerable {

        EntityType<T> get();

        ItemBase item();
    }

    private static class DefaultCartType<T extends Cart> implements CartType<T> {

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
            builder.register(Registry.ENTITY_TYPE_REGISTRY, this.type);
            builder.register(Registry.ITEM_REGISTRY, this.item);
        }

        @Override
        public void initTracking(RegistrationContext ctx, TrackingCollector builder) throws ReflectiveOperationException {
            builder.track(ForgeRegistries.ENTITIES, DefaultCartType.class.getDeclaredField("type"));
            builder.track(ForgeRegistries.ITEMS, DefaultCartType.class.getDeclaredField("item"));
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void registerClient(SetupContext ctx) {
            EntityRenderers.register(this.type, context -> new MinecartRendererX<>(context, ModelLayers.MINECART));
        }
    }
}
