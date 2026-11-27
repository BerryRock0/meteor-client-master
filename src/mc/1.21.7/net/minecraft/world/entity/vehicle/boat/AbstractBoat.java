/* Compatibility alias for the 26.x AbstractBoat package move. */
package net.minecraft.world.entity.vehicle.boat;

public abstract class AbstractBoat extends net.minecraft.world.entity.vehicle.AbstractBoat {
    public AbstractBoat(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.vehicle.AbstractBoat> type,
                        net.minecraft.world.level.Level level, java.util.function.Supplier<net.minecraft.world.item.Item> item) {
        super(type, level, item);
    }
}
