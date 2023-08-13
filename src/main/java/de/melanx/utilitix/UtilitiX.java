package de.melanx.utilitix;

import de.melanx.utilitix.client.ClientUtilitiX;
import de.melanx.utilitix.content.BetterMending;
import de.melanx.utilitix.content.shulkerboat.ShulkerBoatRenderer;
import de.melanx.utilitix.content.slime.SlimyCapability;
import de.melanx.utilitix.content.track.carts.piston.PistonCartContainerMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartScreen;
import de.melanx.utilitix.network.UtiliNetwork;
import de.melanx.utilitix.registration.ModEntities;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.moddingx.libx.mod.ModXRegistration;
import org.moddingx.libx.registration.RegistrationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Mod("utilitix")
public final class UtilitiX extends ModXRegistration {

    private static UtilitiX instance;
    private static UtiliNetwork network;
    public final Logger logger = LoggerFactory.getLogger(UtilitiX.class);

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

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientUtilitiX::new);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(SlimyCapability::registerCapability);

        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.register(new BetterMending());
        MinecraftForge.EVENT_BUS.addGenericListener(LevelChunk.class, SlimyCapability::attach);
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        if (UtilitiXConfig.illusionerInRaid) {
            Raid.RaiderType.create("utilitix_illusioner", EntityType.ILLUSIONER, new int[]{0, 5, 0, 2, 0, 2, 0, 3});
        }
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
        MenuScreens.register(PistonCartContainerMenu.TYPE, PistonCartScreen::new);
        EntityRenderers.register(ModEntities.shulkerBoat, ShulkerBoatRenderer::new);
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
        builder.enableRegistryTracking();
    }
}
