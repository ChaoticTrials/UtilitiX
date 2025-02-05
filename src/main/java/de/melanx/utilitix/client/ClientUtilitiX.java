package de.melanx.utilitix.client;

import de.melanx.utilitix.content.bell.ItemMobBell;
import de.melanx.utilitix.content.slime.SlimeRender;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModKeys;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientUtilitiX {

    public ClientUtilitiX() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerKey);

        MinecraftForge.EVENT_BUS.addListener(SlimeRender::renderWorld);
        MinecraftForge.EVENT_BUS.register(new ClientEventListener());
    }

    private void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF000000 | ItemMobBell.getColor(stack) : 0xFFFFFFFF, ModItems.mobBell);
        event.register(((stack, tintIndex) -> tintIndex != 0 ? -1 : ((DyeableLeatherItem) stack.getItem()).getColor(stack)), ModItems.backpack); // todo remove in 1.21
    }

    private void registerKey(RegisterKeyMappingsEvent event) {
        event.register(ModKeys.OPEN_BACKPACK.get());
    }
}
