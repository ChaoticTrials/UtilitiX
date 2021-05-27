package de.melanx.utilitix.content.rails;

import com.google.common.collect.ImmutableSet;
import de.melanx.utilitix.block.ModProperties;
import de.melanx.utilitix.registration.ModItems;
import io.github.noeppi_noeppi.libx.inventory.container.GenericContainer;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class BlockFilterRail extends BlockRail {
    
    private final TileEntityType<TileFilterRail> teType;

    public BlockFilterRail(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockFilterRail(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, properties, itemProperties);
        //noinspection ConstantConditions
        this.teType = new TileEntityType<>(() -> new TileFilterRail(this.getTileType()), ImmutableSet.of(this), null);
    }

    @Override
    public Set<Object> getAdditionalRegisters() {
        return ImmutableSet.builder().addAll(super.getAdditionalRegisters()).add(this.teType).build();
    }

    @Nonnull
    @Override
    public Property<RailShape> getShapeProperty() {
        return ModProperties.RAIL_SHAPE_FLAT_STRAIGHT;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    
    @Nullable
    @Override
    public TileFilterRail createTileEntity(BlockState state, IBlockReader world) {
        return this.teType.create();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(ModProperties.REVERSE);
        builder.add(ModProperties.RAIL_SIDE);
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && held.getItem() == ModItems.minecartTinkerer) {
            if (!world.isRemote && player instanceof ServerPlayerEntity) {
                TileFilterRail tile = this.getTile(world, pos);
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

    @Override
    public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) return null;
        Direction direction = context.getPlacementHorizontalFacing();
        Pair<RailShape, Boolean> properties = RailUtil.getForPlacement(direction);
        state = state.with(this.getShapeProperty(), properties.getLeft())
                .with(ModProperties.REVERSE, properties.getRight());
        Vector3d hitVec = context.getHitVec();
        double xd = hitVec.x - context.getPos().getX();
        double zd = hitVec.z - context.getPos().getZ();
        boolean side = (direction.getXOffset() >= 0 || !(zd < 0.5)) && (direction.getXOffset() <= 0 || !(zd > 0.5)) && (direction.getZOffset() >= 0 || !(xd > 0.5)) && (direction.getZOffset() <= 0 || !(xd < 0.5));
        return state.with(ModProperties.RAIL_SIDE, !side);
    }

    @Nonnull
    @Override
    public RailShape getRailDirection(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nullable AbstractMinecartEntity cart) {
        RailShape baseShape = state.get(this.getShapeProperty());
        ItemStack filterCart = cart == null ? ItemStack.EMPTY : ItemMinecartTinkerer.getLabelStack(cart);
        if (filterCart.isEmpty()) return baseShape;
        ItemStack filterThis = this.getTile(world, pos).getFilterStack();
        if (filterThis.isEmpty()) return baseShape;
        if (!ItemStack.areItemsEqual(filterThis, filterCart) || !ItemStack.areItemStackTagsEqual(filterThis, filterCart)) {
            return baseShape;
        }
        boolean reverse = state.get(ModProperties.REVERSE);
        boolean side = state.get(ModProperties.RAIL_SIDE);
        if (baseShape == RailShape.NORTH_SOUTH) {
            if (reverse && side) {
                return RailShape.NORTH_WEST;
            } else if (reverse) {
                return RailShape.NORTH_EAST;
            } else if (side) {
                return RailShape.SOUTH_WEST;
            } else {
                return RailShape.SOUTH_EAST;
            }
        } else {
            if (reverse && side) {
                return RailShape.NORTH_WEST;
            } else if (reverse) {
                return RailShape.SOUTH_WEST;
            } else if (side) {
                return RailShape.NORTH_EAST;
            } else {
                return RailShape.SOUTH_EAST;
            }
        }
    }

    public TileFilterRail getTile(IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileFilterRail) {
            return (TileFilterRail) te;
        } else {
            throw new IllegalStateException("Expected a tile entity of type " + TileFilterRail.class + " at " + world + " " + pos + ", got" + te);
        }
    }

    public TileEntityType<TileFilterRail> getTileType() {
        return this.teType;
    }
}
