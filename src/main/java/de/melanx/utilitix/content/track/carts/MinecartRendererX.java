package de.melanx.utilitix.content.track.carts;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;

public class MinecartRendererX<T extends EntityCart> extends MinecartRenderer<T> {

    public MinecartRendererX(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
    }
}
