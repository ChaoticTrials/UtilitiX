package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoat;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import org.moddingx.libx.annotation.registration.RegisterClass;

import java.util.function.Supplier;

@RegisterClass(registry = "ENTITY_TYPE")
public class ModEntities {

    public static final EntityType<ShulkerBoat> oakShulkerBoat = ModEntities.shulkerBoat("oak_shulker_boat", () -> ModItems.oakShulkerBoat);
    public static final EntityType<ShulkerBoat> spruceShulkerBoat = ModEntities.shulkerBoat("spruce_shulker_boat", () -> ModItems.spruceShulkerBoat);
    public static final EntityType<ShulkerBoat> birchShulkerBoat = ModEntities.shulkerBoat("birch_shulker_boat", () -> ModItems.birchShulkerBoat);
    public static final EntityType<ShulkerBoat> jungleShulkerBoat = ModEntities.shulkerBoat("jungle_shulker_boat", () -> ModItems.jungleShulkerBoat);
    public static final EntityType<ShulkerBoat> acaciaShulkerBoat = ModEntities.shulkerBoat("acacia_shulker_boat", () -> ModItems.acaciaShulkerBoat);
    public static final EntityType<ShulkerBoat> cherryShulkerBoat = ModEntities.shulkerBoat("cherry_shulker_boat", () -> ModItems.cherryShulkerBoat);
    public static final EntityType<ShulkerBoat> darkOakShulkerBoat = ModEntities.shulkerBoat("dark_oak_shulker_boat", () -> ModItems.darkOakShulkerBoat);
    public static final EntityType<ShulkerBoat> mangroveShulkerBoat = ModEntities.shulkerBoat("mangrove_shulker_boat", () -> ModItems.mangroveShulkerBoat);
    public static final EntityType<ShulkerBoat> paleOakShulkerBoat = ModEntities.shulkerBoat("pale_oak_shulker_boat", () -> ModItems.paleOakShulkerBoat);
    public static final EntityType<ShulkerBoat> bambooShulkerRaft = ModEntities.shulkerBoat("bamboo_shulker_raft", () -> ModItems.bambooShulkerRaft, true);

    private static EntityType<ShulkerBoat> shulkerBoat(String id, Supplier<Item> dropItem, boolean raft) {
        return EntityType.Builder.<ShulkerBoat>of((type, level) -> new ShulkerBoat(type, level, dropItem, raft), MobCategory.MISC)
                .noLootTable()
                .sized(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .clientTrackingRange(10)

                .build(ResourceKey.create(Registries.ENTITY_TYPE, UtilitiX.getInstance().id(id)));
    }

    private static EntityType<ShulkerBoat> shulkerBoat(String id, Supplier<Item> dropItem) {
        return shulkerBoat(id, dropItem, false);
    }
}
