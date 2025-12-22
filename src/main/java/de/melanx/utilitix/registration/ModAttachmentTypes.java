package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.slime.StickyChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ATTACHMENT_TYPES")
public class ModAttachmentTypes {

    /**
     * Chunk attachment that stores per-block face glue flags.
     * <p>
     * Important: the StickyChunk instance must be attached to its owning LevelChunk
     * so it can sync changes to tracking players.
     */
    public static final AttachmentType<StickyChunk> stickyChunk = AttachmentType.serializable(holder -> {
        StickyChunk data = new StickyChunk();
        if (holder instanceof LevelChunk chunk) {
            data.attach(chunk);
        }
        return data;
    }).build();
}
