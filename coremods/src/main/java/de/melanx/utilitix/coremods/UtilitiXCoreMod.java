package de.melanx.utilitix.coremods;

import cpw.mods.modlauncher.api.ITransformer;
import net.neoforged.neoforgespi.coremod.ICoreMod;

import java.util.List;

public class UtilitiXCoreMod implements ICoreMod {

    @Override
    public Iterable<? extends ITransformer<?>> getTransformers() {
        return List.of(
                new CrouchNoWaterlog(),
                new SignalGetter()
        );
    }
}
