package de.melanx.utilitix;

import de.melanx.utilitix.config.ArmorStandRotationMapper;
import de.melanx.utilitix.content.BetterMending;
import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.slime.SlimeRender;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.track.carts.piston.PistonCartContainerMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartScreen;
import de.melanx.utilitix.network.UtiliNetwork;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.config.ConfigManager;
import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import io.github.noeppi_noeppi.libx.mod.registration.RegistrationBuilder;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
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
public final class UtilitiX extends ModXRegistration {

    private static UtilitiX instance;
    private static UtiliNetwork network;

    public UtilitiX() {
        super(new CreativeModeTab("utilitix") {
            @Nonnull
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(ModItems.handBell);
            }
        });

        instance = this;
        network = new UtiliNetwork(this);

        ConfigManager.registerValueMapper("utilitix", new ArmorStandRotationMapper());
        ConfigManager.registerConfig(new ResourceLocation(this.modid, "common"), UtilitiXConfig.class, false);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColors);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Textures::registerTextures);

            MinecraftForge.EVENT_BUS.addListener(SlimeRender::renderWorld);
        });

        FMLJavaModLoadingContext.get().getModEventBus().addListener(SlimyCapability::registerCapability);

        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.register(new BetterMending());
        MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, SlimyCapability::attach);

        Raid.RaiderType.create("utilitix_illusioner", EntityType.ILLUSIONER, new int[]{0, 5, 0, 2, 0, 2, 0, 3});
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        // 
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(PistonCartContainerMenu.TYPE, PistonCartScreen::new);
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

    @Override
    protected void initRegistration(RegistrationBuilder builder) {
        builder.setVersion(1);
    }
}
