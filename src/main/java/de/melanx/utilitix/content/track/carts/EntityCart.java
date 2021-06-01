package de.melanx.utilitix.content.track.carts;

import com.google.common.collect.ImmutableSet;
import de.melanx.utilitix.UtilitiX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EntityCart extends AbstractMinecartEntity {

    private static final Map<EntityType<?>, Item> cartItems = Collections.synchronizedMap(new HashMap<>());
    
    protected EntityCart(EntityType<?> type, World world) {
        super(type, world);
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
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static <T extends EntityCart> CartType<T> type(String id, EntityType.IFactory<T> factory) {
        return type(id, factory, new Item.Properties().maxStackSize(1));
    }
    
	public static <T extends EntityCart> CartType<T> type(String id, EntityType.IFactory<T> factory, Item.Properties properties) {
        EntityType<T> type = EntityType.Builder.create(factory, EntityClassification.MISC).size(0.98F, 0.7F).trackingRange(8).build(UtilitiX.getInstance().modid + "_" + id);
        ItemBase item = new ItemCart(UtilitiX.getInstance(), type, properties);
        cartItems.put(type, item);
        return new CartType<T>() {

            @Override
            public EntityType<T> get() {
                return type;
            }

            @Override
            public ItemBase item() {
                return item;
            }

            @Override
            public Set<Object> getAdditionalRegisters() {
                return ImmutableSet.of(type, item);
            }

            @Override
            @OnlyIn(Dist.CLIENT)
            public void registerClient(ResourceLocation id, Consumer<Runnable> defer) {
                RenderingRegistry.registerEntityRenderingHandler(type, MinecartRendererX::new);
            }
        };
    }
    
    public interface CartType<T extends EntityCart> extends Registerable {
        
        EntityType<T> get();
        ItemBase item();
    }
}
