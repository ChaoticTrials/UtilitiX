"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var coremods_1 = require("coremods");
function initializeCoreMod() {
    return {
        'NoWaterlog': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.item.BucketItem',
                'methodName': 'm_142073_',
                'methodDesc': '(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;)Z'
            },
            'transformer': function (method) {
                var insertAfter = null;
                var targetLabel = null;
                for (var i = 0; i < method.instructions.size(); i++) {
                    var insn = method.instructions.get(i);
                    if (insn != null && insn.getOpcode() == coremods_1.Opcodes.INVOKEINTERFACE) {
                        var methodInsn = insn;
                        if (methodInsn.owner == 'net/minecraft/world/level/block/LiquidBlockContainer'
                            && methodInsn.name == coremods_1.ASMAPI.mapMethod('m_6044_')
                            && methodInsn.desc == '(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z') {
                            var nextInsn = insn.getNext();
                            if (nextInsn != null && nextInsn.getOpcode() == coremods_1.Opcodes.IFEQ) {
                                insertAfter = nextInsn;
                                targetLabel = nextInsn.label;
                                break;
                            }
                        }
                    }
                }
                if (insertAfter == null || targetLabel == null)
                    return method;
                var target = new coremods_1.InsnList();
                target.add(new coremods_1.VarInsnNode(coremods_1.Opcodes.ALOAD, 1));
                target.add(coremods_1.ASMAPI.buildMethodCall('de/melanx/utilitix/util/CoreUtil', 'shouldPreventWaterlogging', '(Lnet/minecraft/world/entity/player/Player;)Z', coremods_1.ASMAPI.MethodType.STATIC));
                target.add(new coremods_1.JumpInsnNode(coremods_1.Opcodes.IFNE, targetLabel));
                method.instructions.insert(insertAfter, target);
                return method;
            }
        }
    };
}
