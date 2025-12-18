package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.slime.StickyChunk;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.moddingx.libx.annotation.registration.RegisterClass;

@RegisterClass(registry = "ATTACHMENT_TYPES")
public class ModAttachmentTypes {

    public static final AttachmentType<StickyChunk> stickyChunk = AttachmentType.serializable((attachmentHolder) -> new StickyChunk()).build();
}
