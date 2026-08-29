package de.melanx.utilitix.client;

import de.melanx.utilitix.UtilitiX;
import de.melanx.utilitix.client.commands.MapsCommand;
import de.melanx.utilitix.content.bell.BellRenderer;
import de.melanx.utilitix.content.bell.MobBellTintSource;
import de.melanx.utilitix.content.brewery.AdvancedBreweryMenu;
import de.melanx.utilitix.content.brewery.AdvancedBreweryScreen;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceMenu;
import de.melanx.utilitix.content.crudefurnace.CrudeFurnaceScreen;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalMenu;
import de.melanx.utilitix.content.experiencecrystal.ExperienceCrystalScreen;
import de.melanx.utilitix.content.gildingarmor.GildingArmorRecipe;
import de.melanx.utilitix.content.quiver.QuiverMenu;
import de.melanx.utilitix.content.quiver.QuiverScreen;
import de.melanx.utilitix.content.track.carts.piston.PistonCartMenu;
import de.melanx.utilitix.content.track.carts.piston.PistonCartScreen;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererMenu;
import de.melanx.utilitix.content.track.tinkerer.MinecartTinkererScreen;
import de.melanx.utilitix.network.handler.OpenCurioBackpack;
import de.melanx.utilitix.network.handler.StickyChunkRequest;
import de.melanx.utilitix.registration.ModBlocks;
import de.melanx.utilitix.registration.ModKeys;
import de.melanx.utilitix.util.XPUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.tooltip.TooltipLocation;
import net.neoforged.neoforge.event.RegisterTooltipAppendersEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = "utilitix", value = Dist.CLIENT)
public class ClientEventListener {

    public static final MutableComponent GILDED = Component.translatable("tooltip.utilitix.gilded").withStyle(ChatFormatting.GOLD);

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("utilitix")
                .then(MapsCommand.register()));
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderer(RegisterSpecialModelRendererEvent event) {
        event.register(UtilitiX.getInstance().id("bell"), BellRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(UtilitiX.getInstance().id("mob_bell"), MobBellTintSource.CODEC);
    }

    @SubscribeEvent
    public static void registerItemModelProperties(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(UtilitiX.getInstance().id("ancient_compass_angle"), AncientCompassAngleProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(PistonCartMenu.TYPE, PistonCartScreen::new);
        event.register(AdvancedBreweryMenu.TYPE, AdvancedBreweryScreen::new);
        event.register(CrudeFurnaceMenu.TYPE, CrudeFurnaceScreen::new);
        event.register(ExperienceCrystalMenu.TYPE, ExperienceCrystalScreen::new);
        event.register(MinecartTinkererMenu.TYPE, MinecartTinkererScreen::new);
        event.register(QuiverMenu.TYPE, QuiverScreen::new);
    }

    @SubscribeEvent
    public static void loadChunk(ChunkEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            ClientPacketDistributor.sendToServer(new StickyChunkRequest.Message(event.getChunk().getPos()));
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        while (ModKeys.OPEN_BACKPACK.get().consumeClick() && Minecraft.getInstance().screen == null) {
            ClientPacketDistributor.sendToServer(new OpenCurioBackpack.Message());
        }
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ModKeys.OPEN_BACKPACK.get());
    }

    @SubscribeEvent
    public static void registerTooltipAppenders(RegisterTooltipAppendersEvent event) {
        event.registerAppender(TooltipLocation.TAIL, (stack, _, _, _, _, tooltip) -> {
            if (!(stack.getItem() instanceof BlockItem blockItem) || blockItem.getBlock() != ModBlocks.experienceCrystal) {
                return;
            }

            TypedEntityData<BlockEntityType<?>> blockEntityData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
            if (blockEntityData == null || !blockEntityData.contains("Xp")) {
                return;
            }

            int xp = blockEntityData.copyTagWithoutId().getIntOr("Xp", 0);
            if (xp <= 0) {
                return;
            }

            int level = XPUtils.getLevelExp(xp).getLeft();
            if (level > 0) {
                tooltip.accept(Component.translatable("tooltip.utilitix.experience_crystal.xp_level", level).withStyle(ChatFormatting.GREEN));
            } else {
                tooltip.accept(Component.translatable("tooltip.utilitix.experience_crystal.xp_points", xp).withStyle(ChatFormatting.GREEN));
            }
        });
    }

    @SubscribeEvent
    public static void onRenderTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (GildingArmorRecipe.isGilded(stack)) {
            event.getToolTip().add(Math.min(event.getToolTip().size() - 1, 1), GILDED);
        }
    }
}
