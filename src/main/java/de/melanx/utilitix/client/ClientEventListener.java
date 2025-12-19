package de.melanx.utilitix.client;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.client.commands.MapsCommand;
import de.melanx.utilitix.content.bell.RenderBell;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.content.track.carts.piston.PistonCartContainerMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartScreen;
import de.melanx.utilitix.network.StickyChunkRequest;
import de.melanx.utilitix.registration.ModDataComponentTypes;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.commands.Commands;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.Objects;

@EventBusSubscriber(modid = "utilitix", value = Dist.CLIENT)
public class ClientEventListener {

    public static final MutableComponent GILDED = Component.translatable("tooltip.utilitix.gilded").withStyle(ChatFormatting.GOLD);
    public static final ModelLayerLocation SHULKER_BOAT = new ModelLayerLocation(UtilitiX.getInstance().resource("shulker_boat"), "main");

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utilitix")
                .then(MapsCommand.register()));
    }

    @SubscribeEvent
    public static void registerLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SHULKER_BOAT, BoatModel::createBodyModel);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PistonCartContainerMenu.TYPE, PistonCartScreen::new);
//        event.register(BlockEntityMenu.createMenuType(ContainerMenuAdvancedBrewery::new), ScreenAdvancedBrewery::new); todo
//        event.register(BlockEntityMenu.createMenuType(ContainerMenuExperienceCrystal::new), ScreenExperienceCrystal::new);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ItemProperties.register(ModItems.ancientCompass, ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction((level, stack, entity) -> {
            if (!stack.has(ModDataComponentTypes.ancientCityPos) || !stack.has(ModDataComponentTypes.ancientCityLevel)) {
                return null;
            }

            return GlobalPos.of(
                    ResourceKey.create(
                            Registries.DIMENSION,
                            Objects.requireNonNull(ResourceLocation.tryParse(Objects.requireNonNull(stack.get(ModDataComponentTypes.ancientCityLevel)).toString()))
                    ),
                    Objects.requireNonNull(stack.get(ModDataComponentTypes.ancientCityPos))
            );
        }));
        ItemProperties.register(ModItems.mobYoinker, UtilitiX.getInstance().resource("filled"), ((stack, level, entity, seed) -> stack.getOrDefault(ModDataComponentTypes.filled, false) ? 1.0F : 0.0F));
        event.registerItem(new IClientItemExtensions() {
            @Nonnull
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new RenderBell(new BlockEntityRendererProvider.Context(
                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getBlockRenderer(),
                        Minecraft.getInstance().getItemRenderer(),
                        Minecraft.getInstance().getEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels(),
                        Minecraft.getInstance().font
                ));
            }
        }, ModItems.handBell, ModItems.mobBell);
    }

    @SubscribeEvent
    public static void loadChunk(ChunkEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            PacketDistributor.sendToServer(new StickyChunkRequest.Message(event.getChunk().getPos()));
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (GildingArmorRecipe.isGilded(stack)) {
            event.getToolTip().add(Math.min(event.getToolTip().size() - 1, 1), GILDED);
        }
    }
}
