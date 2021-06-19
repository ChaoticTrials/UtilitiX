package de.melanx.utilitix.registration;

import de.melanx.utilitix.content.experiencecrystal.FluidExperience;
import io.github.noeppi_noeppi.libx.annotation.RegName;
import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;

@RegisterClass(priority = 1)
public class ModFluids {

    public static final FluidExperience.Source liquidExperience = new FluidExperience.Source();
    public static final FluidExperience.Flowing liquidExperienceFlowing = new FluidExperience.Flowing();
    @RegName("liquid_experience")
    public static final FlowingFluidBlock liquidExperienceBlock = new FlowingFluidBlock(() -> liquidExperience, AbstractBlock.Properties.from(Blocks.WATER).hardnessAndResistance(500));
}
