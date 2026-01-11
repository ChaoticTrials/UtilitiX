package de.melanx.utilitix.coremods;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TargetType;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.neoforged.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

public class CrouchNoWaterlog implements ITransformer<MethodNode> {

    @Nonnull
    @Override
    public MethodNode transform(MethodNode methodNode, ITransformerVotingContext iTransformerVotingContext) {
        JumpInsnNode insertAfter = null;
        LabelNode targetLabel = null;

        for (int i = 0; i < methodNode.instructions.size(); i++) {
            AbstractInsnNode insnNode = methodNode.instructions.get(i);
            if (insnNode != null && insnNode.getOpcode() == Opcodes.INVOKEINTERFACE) {
                MethodInsnNode methodInsn = (MethodInsnNode) insnNode;
                if (
                        Objects.equals(methodInsn.owner, "net/minecraft/world/level/block/LiquidBlockContainer")
                                && Objects.equals(methodInsn.name, "canPlaceLiquid")
                                && Objects.equals(methodInsn.desc, "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z")
                ) {
                    AbstractInsnNode next = insnNode.getNext();
                    if (next != null && next.getOpcode() == Opcodes.IFEQ) {
                        insertAfter = (JumpInsnNode) next;
                        targetLabel = insertAfter.label;
                        break;
                    }
                }
            }
        }

        if (insertAfter == null || targetLabel == null) {
            ASMAPI.log("DEBUG", "No appropriate instruction found. Exiting transformation.");
            return methodNode;
        }

        InsnList target = new InsnList();
        target.add(new VarInsnNode(Opcodes.ALOAD, 1));
        target.add(ASMAPI.buildMethodCall(
                "de/melanx/utilitix/util/CoreUtil",
                "shouldPreventWaterlogging",
                "(Lnet/minecraft/world/entity/player/Player;)Z",
                ASMAPI.MethodType.STATIC
        ));
        target.add(new JumpInsnNode(Opcodes.IFNE, targetLabel));
        methodNode.instructions.insert(insertAfter, target);

        return methodNode;
    }

    @Nonnull
    @Override
    public TransformerVoteResult castVote(ITransformerVotingContext iTransformerVotingContext) {
        return TransformerVoteResult.YES;
    }

    @Nonnull
    @Override
    public Set<Target<MethodNode>> targets() {
        return Set.of(
                Target.targetMethod(
                        "net.minecraft.world.item.BucketItem",
                        "emptyContents",
                        "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z"
                )
        );
    }

    @Nonnull
    @Override
    public TargetType<MethodNode> getTargetType() {
        return TargetType.METHOD;
    }
}
