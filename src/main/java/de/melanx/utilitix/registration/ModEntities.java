package de.melanx.utilitix.registration;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ENTITY_TYPE")
public class ModEntities {

    public static final EntityType<ShulkerBoat> shulkerBoat = EntityType.Builder.<ShulkerBoat>of(ShulkerBoat::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(UtilitiX.getInstance().modid + "_shulker_boat");
}
