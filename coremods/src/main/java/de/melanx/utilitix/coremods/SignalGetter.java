package de.melanx.utilitix.coremods;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TargetType;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import net.neoforged.coremod.api.ASMAPI;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import javax.annotation.Nonnull;
import java.util.Set;

public class SignalGetter implements ITransformer<MethodNode> {

    @Nonnull
    @Override
    public MethodNode transform(MethodNode methodNode, ITransformerVotingContext iTransformerVotingContext) {
        methodNode.instructions.clear();
        InsnList target = new InsnList();
        target.add(new VarInsnNode(Opcodes.ALOAD, 0));
        target.add(new VarInsnNode(Opcodes.ALOAD, 1));
        target.add(ASMAPI.buildMethodCall(
                "de/melanx/utilitix/util/CoreUtil",
                "getBestNeighborSignalEdit",
                "(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;)I",
                ASMAPI.MethodType.STATIC
        ));
        target.add(new InsnNode(Opcodes.IRETURN));
        methodNode.instructions.add(target);

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
                        "net.minecraft.world.level.SignalGetter",
                        "getBestNeighborSignal",
                        "(Lnet/minecraft/core/BlockPos;)I"
                )
        );
    }

    @Nonnull
    @Override
    public TargetType<MethodNode> getTargetType() {
        return TargetType.METHOD;
    }
}
