"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var coremods_1 = require("coremods");
var coremods_2 = require("./coremods");
function initializeCoreMod() {
    return {
        'NoWaterlog': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.SignalGetter',
                'methodName': 'm_277086_',
                'methodDesc': '(Lnet/minecraft/core/BlockPos;)I'
            },
            'transformer': function (method) {
                method.instructions.clear();
                var target = new coremods_1.InsnList();
                target.add(new coremods_1.VarInsnNode(coremods_1.Opcodes.ALOAD, 0));
                target.add(new coremods_1.VarInsnNode(coremods_1.Opcodes.ALOAD, 1));
                target.add(coremods_1.ASMAPI.buildMethodCall('de/melanx/utilitix/util/CoreUtil', 'getBestNeighborSignalEdit', '(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;)I', coremods_1.ASMAPI.MethodType.STATIC));
                target.add(new coremods_2.InsnNode(coremods_1.Opcodes.IRETURN));
                method.instructions.add(target);
                return method;
            }
        }
    };
}
