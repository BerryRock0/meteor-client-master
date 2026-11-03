package com.viaversion.viaversion.libs.mcstructs.text.serializer.legacy;

import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.components.StringComponent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEventAction;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.AchievementHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.EntityHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.ItemHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.TextHoverEvent;

import java.util.function.Predicate;

public class HoverEventSerializer<T extends HoverEvent> extends EventSerializer<HoverEvent, T, HoverEventAction, TextComponent> {

    public static final HoverEventSerializer<TextHoverEvent> TEXT = createBasic(
            TextHoverEvent.class::isInstance,
            TextHoverEvent::getText,
            HoverEventAction.SHOW_TEXT,
            TextHoverEvent::new
    );
    public static final HoverEventSerializer<AchievementHoverEvent> ACHIEVEMENT = createBasic(
            AchievementHoverEvent.class::isInstance,
            hoverEvent -> new StringComponent(hoverEvent.getStatistic()),
            HoverEventAction.SHOW_ACHIEVEMENT,
            statistic -> new AchievementHoverEvent(statistic.asUnformattedString())
    );
    public static final HoverEventSerializer<ItemHoverEvent> LEGACY_ITEM = createBasic(
            hoverEvent -> hoverEvent instanceof ItemHoverEvent && ((ItemHoverEvent) hoverEvent).isLegacy(),
            hoverEvent -> {
                ItemHoverEvent.LegacyHolder itemData = (ItemHoverEvent.LegacyHolder) hoverEvent.getData();
                return new StringComponent(itemData.getData());
            },
            HoverEventAction.SHOW_ITEM,
            value -> new ItemHoverEvent(value.asUnformattedString())
    );
    public static final HoverEventSerializer<EntityHoverEvent> LEGACY_ENTITY = createBasic(
            hoverEvent -> hoverEvent instanceof EntityHoverEvent && ((EntityHoverEvent) hoverEvent).getData() instanceof EntityHoverEvent.LegacyHolder,
            hoverEvent -> ((EntityHoverEvent.LegacyHolder) hoverEvent.getData()).getData(),
            HoverEventAction.SHOW_ENTITY,
            EntityHoverEvent::new
    );

    private static <T extends HoverEvent> HoverEventSerializer<T> createBasic(final Predicate<HoverEvent> classMatcher, final BasicIOFunction<T, TextComponent> serializer, final HoverEventAction action, final BasicIOFunction<TextComponent, T> deserializer) {
        return new HoverEventSerializer<>(classMatcher, serializer, action, deserializer);
    }

    private static <T extends HoverEvent> HoverEventSerializer<T> createSNbt(final Predicate<HoverEvent> classMatcher, final IOFunction<T, TextComponent> serializer, final HoverEventAction action, final IOFunction<TextComponent, T> deserializer) {
        return new HoverEventSerializer<>(classMatcher, serializer, action, deserializer);
    }

    protected HoverEventSerializer(final Predicate<HoverEvent> classMatcher, final IOFunction<T, TextComponent> serializer, final HoverEventAction action, final IOFunction<TextComponent, T> deserializer) {
        super(classMatcher, serializer, action, deserializer);
    }

}
