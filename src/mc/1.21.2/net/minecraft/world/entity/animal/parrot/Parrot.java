/* Compatibility alias for the 26.x Parrot package move. */
package net.minecraft.world.entity.animal.parrot;

public class Parrot extends net.minecraft.world.entity.animal.Parrot {
    public Parrot(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.animal.Parrot> type,
                  net.minecraft.world.level.Level level) {
        super(type, level);
    }
}
