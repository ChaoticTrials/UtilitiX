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
import {InsnNode} from "./coremods";

function initializeCoreMod(): CoreMods {
    return {
        'NoWaterlog': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.SignalGetter',
                'methodName': 'm_277086_',
                'methodDesc': '(Lnet/minecraft/core/BlockPos;)I'
            },
            'transformer': function (method: MethodNode) {
                method.instructions.clear();

                const target = new InsnList();
                target.add(new VarInsnNode(Opcodes.ALOAD, 0));
                target.add(new VarInsnNode(Opcodes.ALOAD, 1));
                target.add(ASMAPI.buildMethodCall(
                    'de/melanx/utilitix/util/CoreUtil',
                    'getBestNeighborSignalEdit', '(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;)I',
                    ASMAPI.MethodType.STATIC
                ));
                target.add(new InsnNode(Opcodes.IRETURN));

                method.instructions.add(target);
                return method;
            }
        }
    }
}
