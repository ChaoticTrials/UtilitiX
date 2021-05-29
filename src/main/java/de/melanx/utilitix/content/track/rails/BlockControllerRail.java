package de.melanx.utilitix.content.track.rails;

import com.google.common.collect.ImmutableSet;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.inventory.container.GenericContainer;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Function;

public abstract class BlockControllerRail<T extends TileControllerRail> extends BlockRail {

    private final TileEntityType<T> teType;
    public final boolean reinforced;

    public BlockControllerRail(ModX mod, Function<TileEntityType<T>, T> ctor, boolean reinforced, AbstractBlock.Properties properties) {
        this(mod, ctor, reinforced, properties, new Item.Properties());
    }

    public BlockControllerRail(ModX mod, Function<TileEntityType<T>, T> ctor, boolean reinforced, AbstractBlock.Properties properties, Item.Properties itemProperties) {
        super(mod, false, properties, itemProperties);
        //noinspection ConstantConditions
        this.teType = new TileEntityType<>(() -> ctor.apply(this.getTileType()), ImmutableSet.of(this), null);
        this.reinforced = reinforced;
    }

    @Override
    public Set<Object> getAdditionalRegisters() {
        return ImmutableSet.builder().addAll(super.getAdditionalRegisters()).add(this.teType).build();
    }

    @Nonnull
    @Override
    public abstract Property<RailShape> getShapeProperty();

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public T createTileEntity(BlockState state, IBlockReader world) {
        return this.teType.create();
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() == ModItems.minecartTinkerer && player.isSneaking()) {
            if (!world.isRemote && player instanceof ServerPlayerEntity) {
                TileControllerRail tile = this.getTile(world, pos);
                IItemHandlerModifiable handler = new ItemStackHandler(1) {

                    @Override
                    public int getSlotLimit(int slot) {
                        return 1;
                    }

                    @Override
                    protected void onContentsChanged(int slot) {
                        if (slot == 0) {
                            tile.setFilterStack(this.getStackInSlot(0));
                        }
                    }
                };
                handler.setStackInSlot(0, tile.getFilterStack().copy());
                GenericContainer.open((ServerPlayerEntity) player, handler, new TranslationTextComponent("screen.utilitix.minecart_tinkerer"), null);
            }
            return ActionResultType.successOrConsume(world.isRemote);
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.hasTileEntity() && (!state.matchesBlock(newState.getBlock()) || !newState.hasTileEntity())) {
            ItemStack stack = this.getTile(world, pos).getFilterStack();
            if (!stack.isEmpty()) {
                ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, stack.copy());
                world.addEntity(entity);
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    public T getTile(IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
            //noinspection unchecked
            return (T) te;
        } else {
            throw new IllegalStateException("Expected a controller rail tile entity at " + pos + ".");
        }
    }

    public TileEntityType<T> getTileType() {
        return this.teType;
    }

    @Override
    public float getRailMaxSpeed(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart) {
        return this.reinforced ? 0.7f : 0.4f;
    }
}
