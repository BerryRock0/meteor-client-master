package com.viaversion.viaversion.libs.mcstructs.text.stringformat.handling;

import com.viaversion.viaversion.libs.mcstructs.text.TextComponent;
import com.viaversion.viaversion.libs.mcstructs.text.TextFormatting;
import com.viaversion.viaversion.libs.mcstructs.text.stringformat.StringFormat;

/**
 * Option to modify the behavior of how unknown formatting codes are handled in a {@link StringFormat} during serialization.
 *
 * @see StringFormat#toString(TextComponent, ColorHandling, SerializerUnknownHandling)
 */
@FunctionalInterface
public interface SerializerUnknownHandling {

    /**
     * Ignore unknown formatting codes.
     */
    SerializerUnknownHandling IGNORE = (formatting, output) -> null;
    /**
     * Throw an exception when an unknown formatting is found.
     */
    SerializerUnknownHandling THROW = (formatting, output) -> {
        throw new IllegalArgumentException("Unknown formatting: " + formatting);
    };


    /**
     * Handle unknown formatting codes during serialization.<br>
     * Return null to ignore the formatting code.
     *
     * @param formatting The formatting to handle
     * @param output     The output string builder
     * @return The text formatting to use
     */
    TextFormatting handle(final TextFormatting formatting, final StringBuilder output);

}
