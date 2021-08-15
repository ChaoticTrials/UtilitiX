package de.melanx.utilitix.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BoundingBoxUtils {

    public static AABB expand(Entity center, double radius) {
        return expand(center.position(), radius);
    }

    public static AABB expand(Vec3 center, double radius) {
        return new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
    }
}
