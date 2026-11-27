/* Compatibility alias for the 26.x MinecartTNT package move. */
package net.minecraft.world.entity.vehicle.minecart;

public class MinecartTNT extends net.minecraft.world.entity.vehicle.MinecartTNT {
    public MinecartTNT(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.vehicle.MinecartTNT> type,
                       net.minecraft.world.level.Level level) {
        super(type, level);
    }
}
