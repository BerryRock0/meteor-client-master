package com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_21_4;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.viaversion.viaversion.libs.mcstructs.converter.DataConverter;
import com.viaversion.viaversion.libs.mcstructs.converter.codec.Codec;
import com.viaversion.viaversion.libs.mcstructs.converter.codec.map.MapCodecMerger;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_3.NbtConverter_v1_20_3;
import com.viaversion.viaversion.libs.mcstructs.converter.impl.v1_20_5.JsonConverter_v1_20_5;
import com.viaversion.viaversion.libs.mcstructs.converter.mapcodec.MapCodec;
import com.viaversion.viaversion.libs.mcstructs.converter.model.Result;
import com.viaversion.viaversion.libs.mcstructs.core.Identifier;
import com.viaversion.nbt.tag.*;
import com.viaversion.viaversion.libs.mcstructs.snbt.SNbt;
import com.viaversion.viaversion.libs.mcstructs.text.Style;
import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.TextFormatting;
import com.viaversion.viaversion.libs.mcstructs.text.events.click.ClickEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.click.ClickEventAction;
import com.viaversion.viaversion.libs.mcstructs.text.events.click.types.*;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.HoverEventAction;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.EntityHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.ItemHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.events.hover.impl.TextHoverEvent;
import com.viaversion.viaversion.libs.mcstructs.text.font.ResourceFont;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.viaversion.viaversion.libs.mcstructs.text.serializer.verify.VerifyingConverter.isValid;
import static com.viaversion.viaversion.libs.mcstructs.text.serializer.verify.VerifyingConverter.verify;

public class StyleCodecs_v1_21_4 {

    public static final MapCodec<Style> MAP_CODEC = MapCodecMerger.mapCodec(
            TextFormattingCodec.CODEC.mapCodec("color").optional().defaulted(null), Style::getColor,
            ExtraCodecs_v1_21_4.ARGB_COLOR.mapCodec("shadow_color").optional().defaulted(null), Style::getShadowColor,
            Codec.BOOLEAN.mapCodec("obfuscated").optional().defaulted(null), Style::getObfuscated,
            Codec.BOOLEAN.mapCodec("bold").optional().defaulted(null), Style::getBold,
            Codec.BOOLEAN.mapCodec("strikethrough").optional().defaulted(null), Style::getStrikethrough,
            Codec.BOOLEAN.mapCodec("underlined").optional().defaulted(null), Style::getUnderlined,
            Codec.BOOLEAN.mapCodec("italic").optional().defaulted(null), Style::getItalic,
            ClickEventCodec.CODEC.mapCodec("clickEvent").optional().defaulted(null), Style::getClickEvent,
            HoverEventCodec.CODEC.mapCodec("hoverEvent").optional().defaulted(null), Style::getHoverEvent,
            Codec.STRING.mapCodec("insertion").optional().defaulted(null), Style::getInsertion,
            Codec.STRING_IDENTIFIER.mapCodec("font").optional().defaulted(null).mapThrowing(fontDescription -> {
                if (fontDescription == null) return null;
                return ((ResourceFont) fontDescription).getId();
            }, id -> {
                if (id == null) return null;
                return new ResourceFont(id);
            }), Style::getFont,
            Style::new
    );
    public static final Codec<Style> CODEC = MAP_CODEC.asCodec();


    public static class TextFormattingCodec {
        public static final Codec<TextFormatting> CODEC = Codec.STRING.flatMap(formatting -> Result.success(formatting.serialize()), s -> {
            TextFormatting formatting = TextFormatting.parse(s);
            if (formatting == null) return Result.error("Unknown formatting: " + s);
            if (formatting.isRGBColor() && (formatting.getRgbValue() < 0 || formatting.getRgbValue() > 16777215)) return Result.error("Out of range RGB value: " + s);
            return Result.success(formatting);
        });
    }

    public static class ClickEventCodec {
        public static final MapCodec<OpenUrlClickEvent> OPEN_URL = create(OpenUrlClickEvent::asString, OpenUrlClickEvent::new);
        public static final MapCodec<OpenFileClickEvent> OPEN_FILE = create(OpenFileClickEvent::getPath, OpenFileClickEvent::new);
        public static final MapCodec<RunCommandClickEvent> RUN_COMMAND = create(RunCommandClickEvent::getCommand, RunCommandClickEvent::new);
        public static final MapCodec<SuggestCommandClickEvent> SUGGEST_COMMAND = create(SuggestCommandClickEvent::getCommand, SuggestCommandClickEvent::new);
        public static final MapCodec<ChangePageClickEvent> CHANGE_PAGE = create(ChangePageClickEvent::asString, ChangePageClickEvent::new);
        public static final MapCodec<CopyToClipboardClickEvent> COPY_TO_CLIPBOARD = create(CopyToClipboardClickEvent::getValue, CopyToClipboardClickEvent::new);
        public static final Codec<ClickEvent> CODEC = Codec.named(ClickEventAction.OPEN_URL, ClickEventAction.OPEN_FILE, ClickEventAction.RUN_COMMAND, ClickEventAction.SUGGEST_COMMAND, ClickEventAction.CHANGE_PAGE, ClickEventAction.COPY_TO_CLIPBOARD).verified(action -> {
            if (action.isUserDefinable()) return null;
            return Result.error("The action " + action.getName() + " is not user definable");
        }).typed("action", ClickEvent::getAction, action -> {
            switch (action) {
                case OPEN_URL:
                    return OPEN_URL;
                case OPEN_FILE:
                    return OPEN_FILE;
                case RUN_COMMAND:
                    return RUN_COMMAND;
                case SUGGEST_COMMAND:
                    return SUGGEST_COMMAND;
                case CHANGE_PAGE:
                    return CHANGE_PAGE;
                case COPY_TO_CLIPBOARD:
                    return COPY_TO_CLIPBOARD;
                default:
                    return MapCodec.failing("Unknown click event action: " + action);
            }
        });

        private static <T extends ClickEvent> MapCodec<T> create(final Function<T, String> getter, final MapCodecMerger.I1<String, T> constructor) {
            return MapCodecMerger.mapCodec(Codec.STRING.mapCodec("value").required(), getter, constructor);
        }
    }

    public static class HoverEventCodec {
        private static final String CONTENTS = "contents";

        public static final Codec<HoverEvent> MODERN_CODEC = Codec.named(HoverEventAction.SHOW_TEXT, HoverEventAction.SHOW_ITEM, HoverEventAction.SHOW_ENTITY).verified(action -> {
            if (action.isUserDefinable()) return null;
            return Result.error("The action " + action.getName() + " is not user definable");
        }).typed("action", HoverEvent::getAction, action -> {
            switch (action) {
                case SHOW_TEXT:
                    return Text.MAP_CODEC;
                case SHOW_ITEM:
                    return Item.MAP_CODEC;
                case SHOW_ENTITY:
                    return Entity.MAP_CODEC;
                default:
                    return MapCodec.failing("Unknown hover event action: " + action);
            }
        });
        public static final Codec<HoverEvent> LEGACY_CODEC = Codec.named(HoverEventAction.SHOW_TEXT, HoverEventAction.SHOW_ITEM, HoverEventAction.SHOW_ENTITY).verified(action -> {
            if (action.isUserDefinable()) return null;
            return Result.error("The action " + action.getName() + " is not user definable");
        }).typed("action", HoverEvent::getAction, action -> {
            switch (action) {
                case SHOW_TEXT:
                    return Text.LEGACY_MAP_CODEC;
                case SHOW_ITEM:
                    return Item.LEGACY_MAP_CODEC;
                case SHOW_ENTITY:
                    return Entity.LEGACY_MAP_CODEC;
                default:
                    return MapCodec.failing("Unknown hover event action: " + action);
            }
        });
        public static final Codec<HoverEvent> CODEC = Codec.oneOf(MODERN_CODEC, LEGACY_CODEC);

        private static <T extends HoverEvent> MapCodec<T> createLegacy(final BiFunction<DataConverter<?>, TextComponent, Result<T>> constructor) {
            return TextCodecs_v1_21_4.TEXT.converterFlatMap((dataConverter, t) -> Result.error("Legacy hover events can't be serialized"), constructor).mapCodec("value").required();
        }


        public static class Text {
            public static final MapCodec<TextHoverEvent> MAP_CODEC = TextCodecs_v1_21_4.TEXT.mapCodec("contents").required().map(TextHoverEvent::getText, TextHoverEvent::new);
            public static final MapCodec<TextHoverEvent> LEGACY_MAP_CODEC = createLegacy((converter, component) -> Result.success(new TextHoverEvent(component)));
        }

        public static class Item {
            private static final Codec<Identifier> NON_AIR_ITEM = Codec.STRING_IDENTIFIER
                    .converterVerified(verify(TextVerifier_v1_21_4.class, TextVerifier_v1_21_4::verifyRegistryItem, "Invalid item"))
                    .verified(id -> {
                        if (!id.equals("minecraft", "air")) return null;
                        return Result.error("Item must not be minecraft:air");
                    });
            private static final Codec<ItemHoverEvent.ModernHolder> ITEM_STACK_CODEC = MapCodecMerger.codec(
                    NON_AIR_ITEM.mapCodec("id").required(), ItemHoverEvent.ModernHolder::getId,
                    Codec.minInt(1).mapCodec("count").optional().elseGet(() -> 1), ItemHoverEvent.ModernHolder::getCount,
                    ExtraCodecs_v1_21_4.INLINED_COMPOUND_TAG.converterVerified(verify(TextVerifier_v1_21_4.class, TextVerifier_v1_21_4::verifyDataComponents, "Invalid data components")).mapCodec("components").optional().defaulted(null), ItemHoverEvent.ModernHolder::getTag,
                    ItemHoverEvent.ModernHolder::new
            );
            private static final Codec<ItemHoverEvent.ModernHolder> SIMPLE_ITEM_CODEC = NON_AIR_ITEM.map(ItemHoverEvent.ModernHolder::getId, id -> new ItemHoverEvent.ModernHolder(id, 1, null));
            public static final MapCodec<ItemHoverEvent> MAP_CODEC = Codec.oneOf(ITEM_STACK_CODEC, SIMPLE_ITEM_CODEC).mapCodec("contents").required().map(ItemHoverEvent::asModern, ItemHoverEvent::new);
            public static final MapCodec<ItemHoverEvent> LEGACY_MAP_CODEC = createLegacy((converter, component) -> {
                try {
                    CompoundTag tag = SNbt.V1_14.deserialize(component.asUnformattedString());
                    return ITEM_STACK_CODEC.deserialize(converter.fork(NbtConverter_v1_20_3.INSTANCE), tag).map(ItemHoverEvent::new);
                } catch (Throwable t) {
                    return Result.error(t);
                }
            });
        }

        public static class Entity {
            public static final MapCodec<EntityHoverEvent> MAP_CODEC = MapCodecMerger.codec(
                    Codec.STRING_IDENTIFIER.converterVerified(verify(TextVerifier_v1_21_4.class, TextVerifier_v1_21_4::verifyRegistryEntity, "Invalid entity")).mapCodec("type").required(), EntityHoverEvent.ModernHolder::getType,
                    ExtraCodecs_v1_21_4.LENIENT_UUID.mapCodec("id").required(), EntityHoverEvent.ModernHolder::getUuid,
                    TextCodecs_v1_21_4.TEXT.mapCodec("name").optional().defaulted(null), EntityHoverEvent.ModernHolder::getName,
                    EntityHoverEvent.ModernHolder::new
            ).mapCodec("contents").required().map(EntityHoverEvent::asModern, EntityHoverEvent::new);
            public static final MapCodec<EntityHoverEvent> LEGACY_MAP_CODEC = createLegacy((converter, component) -> {
                return com.viaversion.viaversion.libs.mcstructs.text.serializer.v1_20_3.StyleCodecs_v1_20_3.getEntityHoverEventResult(converter, component, TextCodecs_v1_21_4.TEXT, JsonConverter_v1_20_5.INSTANCE);
            });
        }
    }

}
