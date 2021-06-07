package de.melanx.utilitix.util;

import io.github.noeppi_noeppi.libx.block.DirectionShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

import java.util.ArrayList;
import java.util.List;

public class NewDirectionShape extends DirectionShape {

    protected final VoxelShape up;
    protected final VoxelShape down;

    /**
     * Creates a new RotationShape with the given base shape. The base shape should be the shape
     * facing up.
     */
    public NewDirectionShape(VoxelShape baseShape) {
        super(rotatedV(baseShape));
        this.up = baseShape;
        this.down = rotatedV(super.getShape(Direction.NORTH));
    }

    /**
     * @inheritDoc
     */
    @Override
    public VoxelShape getShape(Direction direction) {
        switch (direction) {
            case UP:
                return this.up;
            case DOWN:
                return this.down;
            default:
                return super.getShape(direction);
        }
    }

    private static VoxelShape rotatedV(VoxelShape src) {
        List<VoxelShape> boxes = new ArrayList<>();
        src.forEachBox((fromX, fromY, fromZ, toX, toY, toZ) -> boxes.add(VoxelShapes.create(fromX, fromZ, 1 - fromY, toX, toZ, 1 - toY)));
        return VoxelShapes.or(VoxelShapes.empty(), boxes.toArray(new VoxelShape[]{})).simplify();
    }
}
