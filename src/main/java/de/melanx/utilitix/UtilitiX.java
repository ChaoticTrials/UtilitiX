package de.melanx.utilitix;

import de.melanx.utilitix.config.ArmorStandRotationListMapper;
import de.melanx.utilitix.config.ArmorStandRotationMapper;
import de.melanx.utilitix.content.BetterMending;
import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.slime.SlimeRender;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.network.UtiliNetwork;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.config.ConfigManager;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
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
    private static UtiliNetwork network;

    public UtilitiX() {
        super("utilitix", new ItemGroup("utilitix") {
            @Nonnull
            @Override
            public ItemStack createIcon() {
                return new ItemStack(ModItems.handBell);
            }
        });

        instance = this;
        network = new UtiliNetwork(this);
        
        this.addRegistrationHandler(SlimyCapability::register);

        ConfigManager.registerValueMapper(new ResourceLocation(this.modid, "armor_stand_rotation"), new ArmorStandRotationMapper());
        ConfigManager.registerValueMapper(new ResourceLocation(this.modid, "armor_stand_rotation_list"), new ArmorStandRotationListMapper());
        ConfigManager.registerConfig(new ResourceLocation(this.modid, "common"), UtilitiXConfig.class, false);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Textures::registerTextures);
            
            MinecraftForge.EVENT_BUS.addListener(SlimeRender::renderWorld);
        });

        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.register(new BetterMending());
        MinecraftForge.EVENT_BUS.addGenericListener(Chunk.class, SlimyCapability::attach);
    }

    @Override
    protected void setup(FMLCommonSetupEvent fmlCommonSetupEvent) {
        // 
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent fmlClientSetupEvent) {
        //
    }

    @OnlyIn(Dist.CLIENT)
    private void registerItemColors(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, idx) -> idx == 1 ? 0xFF000000 | ItemMobBell.getColor(stack) : 0xFFFFFFFF, ModItems.mobBell);
    }

    @Nonnull
    public static UtilitiX getInstance() {
        return instance;
    }

    @Nonnull
    public static UtiliNetwork getNetwork() {
        return network;
    }
}
