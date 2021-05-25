package de.melanx.utilitix.item;

import de.melanx.utilitix.slime.SlimyCapability;
import de.melanx.utilitix.slime.StickyChunk;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.ItemBase;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;

public class ItemGlueBall extends ItemBase {

    public ItemGlueBall(ModX mod, Properties properties) {
        super(mod, properties);
    }

    @Nonnull
    @Override
    public ActionResultType onItemUse(@Nonnull ItemUseContext context) {
        if (!context.getWorld().isRemote) {
            Chunk chunk = context.getWorld().getChunkAt(context.getPos());
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = context.getPos().getX() & 0xF;
                int y = context.getPos().getY();
                int z = context.getPos().getZ() & 0xF;
                if (!glue.get(x, y, z, context.getFace()) && SlimyCapability.canGlue(context.getWorld(), context.getPos(), context.getFace())) {
                    glue.set(x, y, z, context.getFace(), true);
                    chunk.markDirty();
                    if (context.getPlayer() == null || !context.getPlayer().abilities.isCreativeMode) {
                        context.getItem().shrink(1);
                    }
                    if (context.getPlayer() != null) {
                        context.getPlayer().swing(context.getHand(), true);
                    }
                    return ActionResultType.CONSUME;
                }
            }
        }
        return super.onItemUse(context);
    }
}
