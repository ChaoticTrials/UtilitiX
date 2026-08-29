package de.melanx.utilitix.content.track.rails;

import de.melanx.utilitix.content.track.carts.piston.PistonCartMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

public class PistonControllerRailBlockEntity extends ControllerRailBlockEntity {

    private PistonCartMode mode = PistonCartMode.IDLE;

    public PistonControllerRailBlockEntity(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
        super(blockEntityTypeIn, pos, state);
    }

    public PistonCartMode getMode() {
        return this.mode;
    }

    public void setMode(PistonCartMode mode) {
        this.mode = mode;
        this.setChanged();
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);

        String modeName = input.getStringOr("Mode", "");
        try {
            this.mode = PistonCartMode.valueOf(modeName);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            this.mode = PistonCartMode.IDLE;
        }
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.putString("Mode", this.mode.name());
    }
}
