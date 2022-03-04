package de.melanx.utilitix.content.brewery;

import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.PotionOutput;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipes;
import io.github.noeppi_noeppi.libx.base.tile.BlockEntityBase;
import io.github.noeppi_noeppi.libx.base.tile.TickableBlock;
import io.github.noeppi_noeppi.libx.capability.ItemCapabilities;
import io.github.noeppi_noeppi.libx.crafting.recipe.RecipeHelper;
import io.github.noeppi_noeppi.libx.inventory.BaseItemStackHandler;
import io.github.noeppi_noeppi.libx.inventory.VanillaWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

//Ingredient-Slot: 0
//Potion-Ingredient-Slot R: 1
//Potion-Ingredient-Slot L: 2
//Output-Slot: 3
//Blaze-Slot: 4
public class TileAdvancedBrewery extends BlockEntityBase implements TickableBlock {

    public static final int MAX_BREW_TIME = 400;

    private int brewTime = 0;
    private int fuel = 0;

    private final BaseItemStackHandler inventory;
    private final Container vanilla;
    private final LazyOptional<IItemHandler> inventoryTop;
    private final LazyOptional<IItemHandler> inventorySide;
    private final LazyOptional<IItemHandler> inventoryBottom;

    public TileAdvancedBrewery(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
        this.inventory = BaseItemStackHandler.builder(5)
                .contentsChanged(slot -> {
                    this.setChanged();
                    this.setDispatchable();
                })
                .validator(stack -> this.level != null && RecipeHelper.isItemValidInput(this.level.getRecipeManager(), ModRecipes.BREWERY, stack), 0)
                .validator(stack -> stack.is(ModItemTags.BOTTLES), 1, 2, 3)
                .validator(stack -> stack.getItem() == Items.BLAZE_POWDER, 4)
                .slotLimit(1, 1, 2, 3)
                .build();
        this.vanilla = new VanillaWrapper(this.inventory, this::setChanged);
        this.inventoryTop = ItemCapabilities.create(this::getInventory, slot -> false, (slot, stack) -> slot == 0 || slot == 3).cast();
        this.inventorySide = ItemCapabilities.create(this::getInventory, slot -> false, (slot, stack) -> slot == 1 || slot == 2 || slot == 4).cast();
        this.inventoryBottom = ItemCapabilities.create(this::getInventory, slot -> slot == 0 || slot == 1 || slot == 2, (slot, stack) -> false).cast();
    }

    @Override
    public void tick() {
        if (this.level != null && !this.level.isClientSide) {
            if (this.fuel <= 0) {
                ItemStack fuelStack = this.inventory.getStackInSlot(4);
                if (fuelStack.getItem() == Items.BLAZE_POWDER && !fuelStack.isEmpty()) {
                    this.fuel = 20;
                    ItemStack fuelNew = fuelStack.copy();
                    fuelNew.shrink(1);
                    this.inventory.setStackInSlot(4, fuelNew);
                    this.setChanged();
                    this.setDispatchable();
                }
            }
            BreweryRecipe recipe = this.level.getRecipeManager().getRecipeFor(ModRecipes.BREWERY, this.vanilla, this.level).orElse(null);
            if ((this.fuel <= 0 || recipe == null) && this.brewTime > 0) {
                this.brewTime = 0;
                this.setChanged();
                this.setDispatchable();
            } else if (recipe != null && this.fuel >= 0) {
                if (this.brewTime <= 0) {
                    this.setDispatchable();
                }
                this.brewTime = Mth.clamp(this.brewTime + 1, 0, MAX_BREW_TIME);
                if (this.brewTime >= MAX_BREW_TIME) {
                    PotionOutput output = recipe.getPotionResult(this.vanilla);
                    if (output == null || output.getMain().isEmpty()) {
                        this.consumeItem(3);
                    } else {
                        this.inventory.setStackInSlot(3, output.getMain());
                    }
                    this.consumeItem(0);
                    if (output == null || output.getOut1().isEmpty()) {
                        this.consumeItem(1);
                    } else {
                        this.inventory.setStackInSlot(1, output.getOut1());
                    }
                    if (output == null || output.getOut2().isEmpty()) {
                        this.consumeItem(2);
                    } else {
                        this.inventory.setStackInSlot(2, output.getOut2());
                    }
                    this.brewTime = 0;
                    this.fuel -= 1;
                    this.level.playSound(null, this.worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1, 1);
                    this.setDispatchable();
                }
                this.setChanged();
            }
        } else {
            if (this.brewTime > 0 && this.brewTime < MAX_BREW_TIME) {
                this.brewTime += 1;
                if (this.level != null && this.level.getGameTime() % 4 == 0 && this.brewTime < MAX_BREW_TIME - 30) {
                    double xf = 0.5;
                    double zf = 0.15;
                    Direction dir = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
                    if (dir.getAxis() == Direction.Axis.X) {
                        double tmp = xf;
                        xf = zf;
                        zf = tmp;
                    }
                    if (dir.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        xf = 1 - xf;
                        zf = 1 - zf;
                    }
                    this.level.addParticle(ParticleTypes.DRIPPING_WATER, this.worldPosition.getX() + xf, this.worldPosition.getY() + 0.34, this.worldPosition.getZ() + zf, 0, -0.6, 0);
                }
            }
        }
    }

    private void consumeItem(int slot) {
        ItemStack stack = this.inventory.getStackInSlot(slot);
        if (!stack.isEmpty()) {
            if (stack.hasContainerItem()) {
                this.inventory.setStackInSlot(slot, stack.getContainerItem().copy());
            } else if (stack.getItem() == Items.POTION || stack.getItem() == ModItems.failedPotion) {
                // Only give glass bottles for normal potions as other types don't give glass bottles as well.
                this.inventory.setStackInSlot(slot, new ItemStack(Items.GLASS_BOTTLE, stack.getCount()));
            } else if (stack.getCount() <= 1) {
                this.inventory.setStackInSlot(slot, ItemStack.EMPTY);
            } else {
                ItemStack copy = stack.copy();
                copy.shrink(1);
                this.inventory.setStackInSlot(slot, copy);
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return LazyOptional.of(this::getInventory).cast();
            }
            return switch (side) {
                case DOWN -> this.inventoryBottom.cast();
                case UP -> this.inventoryTop.cast();
                default -> this.inventorySide.cast();
            };
        } else {
            return super.getCapability(cap, side);
        }
    }

    @Nonnull
    public IItemHandlerModifiable getInventory() {
        return this.inventory;
    }

    @Nonnull
    public IItemHandlerModifiable getUnrestricted() {
        return this.inventory.getUnrestricted();
    }

    public int getBrewTime() {
        return this.brewTime;
    }

    public int getFuel() {
        return this.fuel;
    }


    @Override
    public void load(@Nonnull CompoundTag nbt) {
        super.load(nbt);
        this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
        this.brewTime = nbt.getInt("brewTime");
        this.fuel = nbt.getInt("fuel");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.put("Inventory", this.inventory.serializeNBT());
        compound.putInt("brewTime", this.brewTime);
        compound.putInt("fuel", this.fuel);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        if (this.level != null && !this.level.isClientSide) {
            nbt.put("Inventory", this.inventory.serializeNBT());
            nbt.putInt("brewTime", this.brewTime);
            nbt.putInt("fuel", this.fuel);
        }
        return nbt;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag nbt) {
        if (this.level != null && this.level.isClientSide) {
            super.handleUpdateTag(nbt);
            this.inventory.deserializeNBT(nbt.getCompound("Inventory"));
            this.brewTime = nbt.getInt("brewTime");
            this.fuel = nbt.getInt("fuel");
        }
    }
}
