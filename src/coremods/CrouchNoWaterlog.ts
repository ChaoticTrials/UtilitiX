import {
    AbstractInsnNode,
    ASMAPI,
    CoreMods,
    InsnList,
    JumpInsnNode,
    LabelNode,
    MethodInsnNode,
    MethodNode,
    Opcodes,
    VarInsnNode
} from "coremods";

function initializeCoreMod(): CoreMods {
    return {
        'NoWaterlog': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.item.BucketItem',
                'methodName': 'm_142073_',
                'methodDesc': '(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z'
            },
            'transformer': function (method: MethodNode) {
                let insertAfter: AbstractInsnNode | null = null;
                let targetLabel: LabelNode | null = null;

                for (let i = 0; i < method.instructions.size(); i++) {
                    const insn = method.instructions.get(i);
                    if (insn != null && insn.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        const methodInsn = insn as MethodInsnNode;
                        if (methodInsn.owner == 'net/minecraft/world/level/block/LiquidBlockContainer'
                            && methodInsn.name == ASMAPI.mapMethod('m_6044_')
                            && methodInsn.desc == '(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z') {
                            const nextInsn = insn.getNext();
                            if (nextInsn != null && nextInsn.getOpcode() == Opcodes.IFEQ) {
                                insertAfter = nextInsn;
                                targetLabel = (nextInsn as JumpInsnNode).label;
                                break;
                            }
                        }
                    }
                }
                if (insertAfter == null || targetLabel == null) return method;

                const target = new InsnList();
                target.add(new VarInsnNode(Opcodes.ALOAD, 1));
                target.add(ASMAPI.buildMethodCall(
                    'de/melanx/utilitix/util/CoreUtil',
                    'shouldPreventWaterlogging', '(Lnet/minecraft/world/entity/player/Player;)Z',
                    ASMAPI.MethodType.STATIC
                ));
                target.add(new JumpInsnNode(Opcodes.IFNE, targetLabel));

                method.instructions.insert(insertAfter, target);
                return method;
            }
        }
    }
}
