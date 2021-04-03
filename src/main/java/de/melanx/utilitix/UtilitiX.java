package de.melanx.utilitix;

import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

@Mod("utilitix")
public class UtilitiX extends ModXRegistration {

    private static UtilitiX instance;

    public UtilitiX() {
        super("utilitix", new ItemGroup("utilitix") {
            @Nonnull
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ModItems.handBell);
            }
        });

        instance = this;

        MinecraftForge.EVENT_BUS.register(new EventListener());
    }

    @Override
    protected void setup(FMLCommonSetupEvent fmlCommonSetupEvent) {
        // no
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent fmlClientSetupEvent) {
        RenderType cutout = RenderType.getCutout();
        RenderTypeLookup.setRenderLayer(ModBlocks.weakRedstoneTorch, cutout);
        RenderTypeLookup.setRenderLayer(ModBlocks.weakRedstoneWallTorch, cutout);
    }

    public static UtilitiX getInstance() {
        return instance;
    }
}
