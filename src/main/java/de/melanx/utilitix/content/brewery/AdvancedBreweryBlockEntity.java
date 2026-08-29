package de.melanx.utilitix.content.brewery;

import de.melanx.utilitix.recipe.BreweryRecipe;
import de.melanx.utilitix.recipe.ItemHandlerRecipeInput;
import de.melanx.utilitix.recipe.PotionOutput;
import de.melanx.utilitix.registration.ModItemTags;
import de.melanx.utilitix.registration.ModItems;
import de.melanx.utilitix.registration.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.moddingx.libx.base.tile.BlockEntityBase;
import org.moddingx.libx.base.tile.TickingBlock;
import org.moddingx.libx.crafting.RecipeHelper;
import org.moddingx.libx.inventory.BaseItemStackHandler;
import org.moddingx.libx.inventory.FilterItemHandler;
import org.moddingx.libx.inventory.IAdvancedItemHandler;
import org.moddingx.libx.inventory.IAdvancedItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Optional;

//Ingredient-Slot: 0
//Potion-Ingredient-Slot R: 1
//Potion-Ingredient-Slot L: 2
//Output-Slot: 3
//Blaze-Slot: 4
public class AdvancedBreweryBlockEntity extends BlockEntityBase implements TickingBlock {

    public static final int MAX_BREW_TIME = 400;
    public static final int INGREDIENT_SLOT = 0;
    public static final int POTION_SLOT_RIGHT = 1;
    public static final int POTION_SLOT_LEFT = 2;
    public static final int OUTPUT_SLOT = 3;
    public static final int FUEL_SLOT = 4;

    private int brewTime = 0;
    private int fuel = 0;

    private final BaseItemStackHandler inventory;
    private final ItemHandlerRecipeInput recipeInput;
    public final IAdvancedItemHandler inventoryTop;
    public final IAdvancedItemHandler inventorySide;
    public final IAdvancedItemHandler inventoryBottom;

    public AdvancedBreweryBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
        this.inventory = BaseItemStackHandler.builder(5)
                .contentsChanged(slot -> {
                    this.setChanged();
                    this.setDispatchable();
                })
                .validator(stack -> !(this.level instanceof ServerLevel serverLevel) || RecipeHelper.isItemValidInput(serverLevel.recipeAccess(), ModRecipeTypes.BREWERY, stack), INGREDIENT_SLOT)
                .validator(stack -> stack.is(ModItemTags.BOTTLES), POTION_SLOT_RIGHT, POTION_SLOT_LEFT, OUTPUT_SLOT)
                .validator(stack -> stack.getItem() == Items.BLAZE_POWDER, FUEL_SLOT)
                .slotLimit(1, POTION_SLOT_RIGHT, POTION_SLOT_LEFT, OUTPUT_SLOT)
                .build();
        this.recipeInput = new ItemHandlerRecipeInput(this.inventory);

        this.inventoryTop = new FilterItemHandler(this.inventory, slot -> false, (slot, stack) -> slot == INGREDIENT_SLOT || slot == OUTPUT_SLOT);
        this.inventorySide = new FilterItemHandler(this.inventory, slot -> false, (slot, stack) -> slot == POTION_SLOT_RIGHT || slot == POTION_SLOT_LEFT || slot == FUEL_SLOT);
        this.inventoryBottom = new FilterItemHandler(this.inventory, slot -> slot == OUTPUT_SLOT || slot == POTION_SLOT_RIGHT || slot == POTION_SLOT_LEFT, (slot, stack) -> false);
    }

    @Override
    public void tick() {
        if (this.level == null || this.level.isClientSide()) {
            this.clientTick();
            return;
        }

        if (this.fuel <= 0) {
            ItemStack fuelStack = ItemUtil.getStack(this.inventory, FUEL_SLOT);
            if (fuelStack.getItem() == Items.BLAZE_POWDER && !fuelStack.isEmpty()) {
                this.fuel = 20;
                ItemStack fuelNew = fuelStack.copy();
                fuelNew.shrink(1);
                this.setStack(FUEL_SLOT, fuelNew);
                this.setChanged();
                this.setDispatchable();
            }
        }

        Optional<RecipeHolder<BreweryRecipe>> recipe = ((ServerLevel) this.level).recipeAccess().getRecipeFor(ModRecipeTypes.BREWERY, this.recipeInput, this.level);
        if ((this.fuel <= 0 || recipe.isEmpty()) && this.brewTime > 0) {
            this.brewTime = 0;
            this.setChanged();
            this.setDispatchable();
            return;

        }

        if (recipe.isEmpty() || this.fuel < 0) {
            return;
        }

        if (this.brewTime <= 0) {
            this.setDispatchable();
        }

        this.brewTime = Mth.clamp(this.brewTime + 1, 0, MAX_BREW_TIME);
        if (this.brewTime < MAX_BREW_TIME) {
            this.setChanged();
            return;
        }

        PotionOutput output = recipe.get().value().getPotionResult(this.recipeInput);
        if (output == null || output.getMain().isEmpty()) {
            this.consumeItem(OUTPUT_SLOT);
        } else {
            this.setStack(OUTPUT_SLOT, output.getMain());
        }

        this.consumeItem(INGREDIENT_SLOT);
        if (output == null || output.getOut1().isEmpty()) {
            this.consumeItem(POTION_SLOT_RIGHT);
        } else {
            this.setStack(POTION_SLOT_RIGHT, output.getOut1());
        }

        if (output == null || output.getOut2().isEmpty()) {
            this.consumeItem(POTION_SLOT_LEFT);
        } else {
            this.setStack(POTION_SLOT_LEFT, output.getOut2());
        }

        this.brewTime = 0;
        this.fuel -= 1;
        this.level.playSound(null, this.worldPosition, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS);

        this.setDispatchable();
        this.setChanged();
    }

    private void clientTick() {
        if (this.brewTime <= 0 || this.brewTime >= MAX_BREW_TIME) {
            return;
        }

        this.brewTime += 1;

        if (this.level == null || this.level.getGameTime() % 4 != 0 || this.brewTime >= MAX_BREW_TIME - 30) {
            return;
        }

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

    private void consumeItem(int slot) {
        ItemStack stack = ItemUtil.getStack(this.inventory, slot);

        if (stack.isEmpty()) {
            return;
        }

        net.minecraft.world.item.ItemStackTemplate remainder = stack.getCraftingRemainder();
        if (remainder != null) {
            this.setStack(slot, remainder.create());
            return;
        }

        if (stack.getItem() == Items.POTION || stack.getItem() == ModItems.failedPotion) {
            // Only give glass bottles for normal potions as other types don't give glass bottles as well.
            this.setStack(slot, new ItemStack(Items.GLASS_BOTTLE, stack.getCount()));
            return;
        }

        if (stack.getCount() <= 1) {
            this.setStack(slot, ItemStack.EMPTY);
            return;
        }

        ItemStack copy = stack.copy();
        copy.shrink(1);
        this.setStack(slot, copy);
    }

    private void setStack(int slot, ItemStack stack) {
        this.inventory.getUnrestricted().set(slot, ItemResource.of(stack), stack.getCount());
    }

    public static ResourceHandler<ItemResource> getCapability(AdvancedBreweryBlockEntity be, Direction side) {
        if (side == null) {
            return be.getInventory();
        }

        return switch(side) {
            case DOWN -> be.inventoryBottom;
            case UP -> be.inventoryTop;
            default -> be.inventorySide;
        };
    }

    @Nonnull
    public BaseItemStackHandler getInventory() {
        return this.inventory;
    }

    @Nonnull
    public IAdvancedItemHandlerModifiable getUnrestricted() {
        return this.inventory.getUnrestricted();
    }

    public int getBrewTime() {
        return this.brewTime;
    }

    public int getFuel() {
        return this.fuel;
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);

        this.inventory.deserialize(input.childOrEmpty("Inventory"));
        this.brewTime = input.getIntOr("brewTime", 0);
        this.fuel = input.getIntOr("fuel", 0);
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);

        this.inventory.serialize(output.child("Inventory"));
        output.putInt("brewTime", this.brewTime);
        output.putInt("fuel", this.fuel);
    }
}
