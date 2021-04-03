package de.melanx.utilitix.data;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.data.provider.ItemModelProviderBase;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProviderBase {
    public ItemModels(DataGenerator generator, ExistingFileHelper helper) {
        super(UtilitiX.getInstance(), generator, helper);
    }

    @Override
    protected void setup() {
        this.manualModel(ModItems.weakRedstoneTorch);
        //noinspection ConstantConditions
        this.itemModelFromBlock(ModItems.weakRedstoneTorch.getRegistryName());
    }

    private void itemModelFromBlock(ResourceLocation id) {
        this.withExistingParent(id.getPath(), GENERATED).texture("layer0", new ResourceLocation(id.getNamespace(), "block/" + id.getPath()));
    }
}
