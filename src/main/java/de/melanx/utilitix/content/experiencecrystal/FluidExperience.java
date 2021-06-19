package de.melanx.utilitix.content.experiencecrystal;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModFluids;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidExperience {

    private static final FluidAttributes.Builder ATTRIBUTES = FluidAttributes.builder(
            new ResourceLocation(UtilitiX.getInstance().modid, "block/liquid_experience"),
            new ResourceLocation(UtilitiX.getInstance().modid, "block/liquid_experience_flowing"))
            .viscosity(2000)
            .temperature(666);
    private static final ForgeFlowingFluid.Properties PROPERTIES = new ForgeFlowingFluid.Properties(() -> ModFluids.liquidExperience, () -> ModFluids.liquidExperienceFlowing, ATTRIBUTES)
            .block(() -> ModFluids.liquidExperienceBlock)
            .bucket(() -> ModItems.liquidExperienceBucket);

    public static class Source extends ForgeFlowingFluid.Source {

        public Source() {
            super(PROPERTIES);
        }
    }

    public static class Flowing extends ForgeFlowingFluid.Flowing {

        public Flowing() {
            super(PROPERTIES);
        }
    }
}
