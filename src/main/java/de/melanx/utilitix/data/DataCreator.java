package de.melanx.utilitix.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = "utilitix", bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataCreator {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(new ModTagProvider(generator, helper));
            generator.addProvider(new RecipeProvider(generator));
            generator.addProvider(new LootTableProvider(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new BlockStateProvider(generator, helper));
            generator.addProvider(new ItemModelProvider(generator, helper));
        }
    }
}
