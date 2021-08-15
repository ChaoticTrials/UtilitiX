package de.melanx.utilitix.content.track.carts;

import com.google.common.collect.ImmutableSet;
import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.base.ItemBase;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

    @Override
    public ItemStack getCartItem() {
        return new ItemStack(cartItems.getOrDefault(this.getType(), Items.MINECART));
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
        return new CartType<>() {

            @Override
            public EntityType<T> get() {
                return type;
            }

            @Override
            public ItemBase item() {
                return item;
            }

            @Override
            public Set<Object> getAdditionalRegisters(ResourceLocation id) {
                return ImmutableSet.of(type, item);
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
//                TODO RenderingRegistry.registerEntityRenderingHandler(type, MinecartRendererX::new);
            }
        };
    }

    public static double getHorizontalDistanceSqr(Vec3 vec) {
        return vec.x * vec.x + vec.z * vec.z;
    }

    public interface CartType<T extends Cart> extends Registerable {

        EntityType<T> get();

        ItemBase item();
    }
}
