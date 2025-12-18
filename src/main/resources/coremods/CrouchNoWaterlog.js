function initializeCoreMod() {
    var ASMAPI = Java.type('net.neoforged.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

    return {
        'CrouchNoWaterlog': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.item.BucketItem',
                'methodName': 'm_142073_',
                'methodDesc': '(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z'
            },
            'transformer': function (method) {
                ASMAPI.log("DEBUG", "Starting transformation for CrouchNoWaterlog");
                var insertAfter = null;
                ASMAPI.log("DEBUG", "Searching for the appropriate instruction to modify");
                var targetLabel = null;
                for (var i = 0; i < method.instructions.size(); i++) {
                    var insn = method.instructions.get(i);
                    ASMAPI.log("DEBUG", "Inspecting instruction at index " + i + " with opcode: " + (insn != null ? insn.getOpcode() : "null"));
                    if (insn != null && insn.getOpcode() == Opcodes.INVOKEINTERFACE) {
                        ASMAPI.log("DEBUG", "Found INVOKEINTERFACE at index " + i);
                        var methodInsn = insn;
                        if (methodInsn.owner == 'net/minecraft/world/level/block/LiquidBlockContainer'
                            && methodInsn.name == ASMAPI.mapMethod('m_6044_')
                            && methodInsn.desc == '(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/Fluid;)Z') {
                            ASMAPI.log("DEBUG", "Matched target method: " + methodInsn.name + methodInsn.desc);
                            var nextInsn = insn.getNext();
                            if (nextInsn != null && nextInsn.getOpcode() == Opcodes.IFEQ) {
                                ASMAPI.log("DEBUG", "Found matching IFEQ instruction at index " + (i + 1));
                                insertAfter = nextInsn;
                                targetLabel = nextInsn.label;
                                break;
                            }
                        }
                    }
                }
                if (insertAfter == null || targetLabel == null) {
                    ASMAPI.log("DEBUG", "No appropriate instruction found. Exiting transformation.");
                    return method;
                }
                ASMAPI.log("DEBUG", "Inserting new instructions after the target point");
                var target = new InsnList();
                target.add(new VarInsnNode(Opcodes.ALOAD, 1));
                ASMAPI.log("DEBUG", "Added ALOAD instruction");
                target.add(ASMAPI.buildMethodCall(
                    'de/melanx/utilitix/util/CoreUtil',
                    'shouldPreventWaterlogging',
                    '(Lnet/minecraft/world/entity/player/Player;)Z',
                    ASMAPI.MethodType.STATIC)
                );
                ASMAPI.log("DEBUG", "Added method call to shouldPreventWaterlogging");
                target.add(new JumpInsnNode(Opcodes.IFNE, targetLabel));
                ASMAPI.log("DEBUG", "Added IFNE jump instruction");
                method.instructions.insert(insertAfter, target);
                ASMAPI.log("DEBUG", "Transformation complete for CrouchNoWaterlog");
                return method;
            }
        }
    };
}
