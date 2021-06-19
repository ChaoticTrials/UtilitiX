package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModFluids;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.FluidTagsProvider;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Objects;

public class FluidTagProvider extends FluidTagsProvider {

    public static Tags.IOptionalNamedTag<Fluid> EXPERIENCE = FluidTags.createOptional(new ResourceLocation("forge", "experience"));

    public FluidTagProvider(DataGenerator generator, @Nullable ExistingFileHelper helper) {
        super(generator, UtilitiX.getInstance().modid, helper);
    }

    @Override
    protected void registerTags() {
        ForgeRegistries.FLUIDS.getValues().stream()
                .filter(i -> UtilitiX.getInstance().modid.equals(Objects.requireNonNull(i.getRegistryName()).getNamespace()))
                .forEach(this::defaultBlockTags);
        this.getOrCreateBuilder(EXPERIENCE).add(ModFluids.liquidExperience, ModFluids.liquidExperienceFlowing);
    }

    private void defaultBlockTags(Fluid fluid) {
        this.getOrCreateBuilder(FluidTags.WATER).add(fluid);
    }
}
