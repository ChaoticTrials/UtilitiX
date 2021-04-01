package de.melanx.utilitix;

import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

public class UtilitiX extends ModX {

    public UtilitiX() {
        super("utilitix", new ItemGroup("utilitix") {
            @Nonnull
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.BARRIER);
            }
        });
    }

    @Override
    protected void setup(FMLCommonSetupEvent fmlCommonSetupEvent) {

    }

    @Override
    protected void clientSetup(FMLClientSetupEvent fmlClientSetupEvent) {

    }
}
