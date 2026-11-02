package com.viaversion.viaversion.libs.mcstructs.text.events.hover;

import com.viaversion.viaversion.libs.mcstructs.core.Identifier;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.AchievementHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.EntityHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.ItemHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.TextHoverEvent;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * The abstract class for hover events.<br>
 * Until Minecraft 1.16 hover events always used the {@link TextHoverEvent} implementation.
 */
public abstract class HoverEvent {

    public static TextHoverEvent text(final TextComponent text) {
        return new TextHoverEvent(text);
    }

    public static AchievementHoverEvent achievement(final String statistic) {
        return new AchievementHoverEvent(statistic);
    }

    public static ItemHoverEvent item(final Identifier item, final int count, @Nullable final CompoundTag nbt) {
        return new ItemHoverEvent(item, count, nbt);
    }

    public static EntityHoverEvent entity(final Identifier entityType, final UUID uuid, @Nullable final TextComponent name) {
        return new EntityHoverEvent(entityType, uuid, name);
    }


    protected HoverEventAction action;

    public HoverEvent(final HoverEventAction action) {
        this.action = action;
    }

    /**
     * @return The action of this hover event
     */
    public HoverEventAction getAction() {
        return this.action;
    }

    @Override
    public abstract boolean equals(final Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();

}
