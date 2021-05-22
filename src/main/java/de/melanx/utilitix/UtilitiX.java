package de.melanx.utilitix;

import de.melanx.utilitix.item.bells.MobBell;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Textures::registerTextures);
        });

        MinecraftForge.EVENT_BUS.register(new EventListener());
    }

    @Override
    protected void setup(FMLCommonSetupEvent fmlCommonSetupEvent) {
        // 
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent fmlClientSetupEvent) {
        RenderTypeLookup.setRenderLayer(ModBlocks.weakRedstoneTorch, RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.weakRedstoneWallTorch, RenderType.getCutout());
    }

    @OnlyIn(Dist.CLIENT)
    private void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, idx) -> idx == 1 ? 0xFF000000 | MobBell.getColor(stack) : 0xFFFFFFFF, ModItems.mobBell);
    }

    public static UtilitiX getInstance() {
        return instance;
    }
}
