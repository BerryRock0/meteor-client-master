/* Compatibility alias for the 26.x AbstractChestedHorse package move. */
package net.minecraft.world.entity.animal.equine;

public abstract class AbstractChestedHorse extends net.minecraft.world.entity.animal.horse.AbstractChestedHorse {
    public AbstractChestedHorse(net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.animal.horse.AbstractChestedHorse> type,
                                net.minecraft.world.level.Level level) {
        super(type, level);
    }
}
