function initializeCoreMod() {
    var ASMAPI = Java.type('net.neoforged.coremod.api.ASMAPI');
    var Opcodes = Java.type('org.objectweb.asm.Opcodes');
    var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
    var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
    var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

    return {
        'SignalGetter': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.level.SignalGetter',
                'methodName': 'getBestNeighborSignal',
                'methodDesc': '(Lnet/minecraft/core/BlockPos;)I'
            },
            'transformer': function (method) {
                method.instructions.clear();
                var target = new InsnList();
                target.add(new VarInsnNode(Opcodes.ALOAD, 0));
                target.add(new VarInsnNode(Opcodes.ALOAD, 1));
                target.add(ASMAPI.buildMethodCall(
                    'de/melanx/utilitix/util/CoreUtil',
                    'getBestNeighborSignalEdit',
                    '(Lnet/minecraft/world/level/SignalGetter;Lnet/minecraft/core/BlockPos;)I',
                    ASMAPI.MethodType.STATIC)
                );
                target.add(new InsnNode(Opcodes.IRETURN));
                method.instructions.add(target);
                return method;
            }
        }
    };
}
