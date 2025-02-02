package cofh.lib.util.raytracer;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * A VoxelShape implementation, produces a {@link VoxelShapeRayTraceResult} when ray traced.
 * Whilst similar to {@link IndexedVoxelShape}, will ray trace each sub-component provided, returning the closest.
 * <p>
 * The sub-component will have its outline automatically rendered appropriately.
 * <p>
 * Copied from CCL with permission :)
 * <p>
 * Created by covers1624 on 5/12/20.
 */
public class MultiIndexedVoxelShape extends VoxelShape {

    private final VoxelShape merged;
    private final ImmutableSet<IndexedVoxelShape> shapes;

    /**
     * Construct a MultiIndexedVoxelShape, using the combination of all the sub-components
     * as this VoxelShape.
     *
     * @param shapes The sub-components.
     */
    public MultiIndexedVoxelShape(ImmutableSet<IndexedVoxelShape> shapes) {

        this(mergeAll(shapes), shapes);
    }

    /**
     * Constructs a MultiIndexedVoxelShape, using the provided VoxelShape as this shape,
     * whilst still RayTracing against all the sub-components.
     *
     * @param merged The base shape.
     * @param shapes The sub-components.
     */
    public MultiIndexedVoxelShape(VoxelShape merged, ImmutableSet<IndexedVoxelShape> shapes) {

        super(merged.shape);
        this.merged = merged;
        this.shapes = shapes;
    }

    @Override
    public DoubleList getCoords(Direction.Axis axis) {

        return merged.getCoords(axis);
    }

    @Nullable
    @Override
    public VoxelShapeRayTraceResult clip(Vector3d start, Vector3d end, BlockPos pos) {

        VoxelShapeRayTraceResult closest = null;
        double dist = Double.MAX_VALUE;
        for (IndexedVoxelShape shape : shapes) {
            VoxelShapeRayTraceResult hit = shape.clip(start, end, pos);
            if (hit != null && dist >= hit.dist) {
                closest = hit;
                dist = hit.dist;
            }
        }
        return closest;
    }

    @SuppressWarnings ("unchecked")
    private static VoxelShape mergeAll(Set<? extends VoxelShape> shapes) {

        Set<VoxelShape> genericsDie = (Set<VoxelShape>) shapes;
        return genericsDie.stream().reduce(VoxelShapes.empty(), VoxelShapes::or);
    }

}
