/* Compatibility shim for the 26.x entity reference API. */
package net.minecraft.world.entity;

import java.util.UUID;

public final class EntityReference<T extends Entity> {
    private final UUID uuid;

    private EntityReference(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() { return uuid; }

    public UUID getUUID() { return uuid; }

    public static <T extends Entity> EntityReference<T> of(T entity) {
        return new EntityReference<>(entity == null ? new UUID(0, 0) : entity.getUUID());
    }

    public static <T extends Entity> T get(EntityReference<T> reference, net.minecraft.world.level.Level level, Class<T> type) {
        if (level == null || reference == null) return null;
        try {
            Object entity = level.getClass().getMethod("getEntity", UUID.class).invoke(level, reference.uuid);
            return type.isInstance(entity) ? type.cast(entity) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
