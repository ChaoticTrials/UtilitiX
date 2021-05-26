package de.melanx.utilitix;

import de.melanx.utilitix.module.bell.ItemMobBell;
import de.melanx.utilitix.module.slime.SlimyCapability;
import de.melanx.utilitix.module.slime.StickyChunk;
import de.melanx.utilitix.network.StickyChunkRequestSerializer;
import de.melanx.utilitix.registration.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {

    private static final IFormattableTextComponent BLACKLISTED_MOB = new TranslationTextComponent("tooltip." + UtilitiX.getInstance().modid + ".blacklisted_mob").mergeStyle(TextFormatting.DARK_RED);

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        PlayerEntity player = event.getPlayer();

        if (player.isSneaking() && player.getHeldItem(event.getHand()).getItem() == ModItems.mobBell && event.getTarget() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getTarget();
            Hand hand = event.getHand();
            ItemStack stack = player.getHeldItem(hand);
            ResourceLocation entityKey = EntityType.getKey(target.getType());
            if (entityKey.toString().equals(stack.getOrCreateTag().getString("Entity"))) {
                return;
            }

            if (UtilitiXConfig.HandBells.blacklist.contains(entityKey)) {
                player.sendStatusMessage(BLACKLISTED_MOB, true);
                return;
            }

            stack.getOrCreateTag().putString("Entity", entityKey.toString());
            player.setHeldItem(hand, stack);
            player.sendStatusMessage(ItemMobBell.getCurrentMob(target.getType()), true);
            event.setCancellationResult(ActionResultType.SUCCESS);
            event.setCanceled(true);
        }
    }

    // TODO wait for https://github.com/MinecraftForge/MinecraftForge/pull/7715
//    @SubscribeEvent
//    public void onBowFindAmmo(PlayerFindProjectileEvent event) {
//        if (event.getFoundAmmo().isEmpty()) {
//            PlayerEntity player = event.getPlayer();
//            Stream.concat(Stream.of(player.getHeldItemOffhand()), player.inventory.mainInventory.stream())
//                    .filter(stack -> stack.getItem() == ModItems.quiver)
//                    .filter(stack -> !Quiver.isEmpty(stack))
//                    .findFirst()
//                    .ifPresent(stack -> {
//                        IItemHandlerModifiable inventory = Quiver.getInventory(stack);
//                        assert inventory != null;
//                        int enchantmentLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
//                        if (enchantmentLevel >= 1) {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    event.setAmmo(arrow.copy());
//                                    return;
//                                }
//                            }
//                        } else {
//                            for (int i = 0; i < inventory.getSlots(); i++) {
//                                ItemStack arrow = inventory.getStackInSlot(i);
//                                if (!arrow.isEmpty()) {
//                                    arrow = player.isCreative() ? arrow.copy() : arrow;
//                                    event.setAmmo(arrow);
//                                    return;
//                                }
//                            }
//                        }
//                    });
//        }
//    }

    @SubscribeEvent
    public void entityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof ArmorStandEntity && event.getTarget().getPersistentData().getBoolean("UtilitiXArmorStand")) {
            if (event.getItemStack().getItem() == Items.FLINT && event.getPlayer().isSneaking()) {
                ArmorStandEntity entity = (ArmorStandEntity) event.getTarget();
                if (UtilitiXConfig.armorStandPoses.size() >= 2) {
                    int newIdx = (entity.getPersistentData().getInt("UtilitiXPoseIdx") + 1) % UtilitiXConfig.armorStandPoses.size();
                    entity.getPersistentData().putInt("UtilitiXPoseIdx", newIdx);
                    UtilitiXConfig.armorStandPoses.get(newIdx).apply(entity);
                }
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void loadChunk(ChunkEvent.Load event) {
        if (event.getWorld().isRemote()) {
            UtilitiX.getNetwork().instance.sendToServer(new StickyChunkRequestSerializer.StickyChunkRequestMessage(event.getChunk().getPos()));
        }
    }

    @SubscribeEvent
    public void neighbourChange(BlockEvent.NeighborNotifyEvent event) {
        if (!event.getWorld().isRemote() && event.getWorld() instanceof World) {
            World world = (World) event.getWorld();
            for (Direction dir : Direction.values()) {
                BlockPos thePos = event.getPos().offset(dir);
                BlockState state = world.getBlockState(thePos);
                if (state.getBlock() == Blocks.MOVING_PISTON && (state.get(BlockStateProperties.FACING) == dir || state.get(BlockStateProperties.FACING) == dir.getOpposite())) {
                    // Block has been changed because of a piston move.
                    // Glue logic is handled in the piston til
                    // Skip this here
                    return ;
                } else if (state.getBlock() == Blocks.PISTON_HEAD && state.get(BlockStateProperties.SHORT) && (state.get(BlockStateProperties.FACING) == dir || state.get(BlockStateProperties.FACING) == dir.getOpposite())) {
                    // Block has been changed because of a piston move.
                    // Glue logic is handled in the piston til
                    // Skip this here
                    // This is sometimes buggy but we can't really do anything about this.
                    return ;
                }
            }
            Chunk chunk = world.getChunkAt(event.getPos());
            //noinspection ConstantConditions
            StickyChunk glue = chunk.getCapability(SlimyCapability.STICKY_CHUNK).orElse(null);
            //noinspection ConstantConditions
            if (glue != null) {
                int x = event.getPos().getX() & 0xF;
                int y = event.getPos().getY();
                int z = event.getPos().getZ() & 0xF;
                for (Direction dir : Direction.values()) {
                    if (glue.get(x, y, z, dir) && !SlimyCapability.canGlue(world, event.getPos(), dir)) {
                        glue.set(x, y, z, dir, false);
                        chunk.markDirty();
                        chunk.markDirty();
                        BlockPos targetPos = event.getPos().offset(dir);
                        ItemEntity ie = new ItemEntity(world, targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5, new ItemStack(ModItems.glueBall));
                        ie.setPickupDelay(20);
                        world.addEntity(ie);
                    }
                }
            }
        }
    }
}
