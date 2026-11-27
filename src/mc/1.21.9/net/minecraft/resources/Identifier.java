/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package net.minecraft.resources;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import net.minecraft.IdentifierException;

/**
 * Compatibility shim: 26.x renamed {@code ResourceLocation} to {@code Identifier}.
 *
 * <p>In all older versions the same concept is called {@code ResourceLocation}. This shim extends
 * it so shared code can use the 26.x name everywhere, while every Minecraft API still receives a
 * genuine {@code ResourceLocation}.</p>
 */
public class Identifier extends ResourceLocation {
    public Identifier(String namespace, String path) { super(namespace, path); }
    public Identifier(String id) { this(namespaceOf(id), pathOf(id)); }

    private static String namespaceOf(String id) {
        int colon = id.indexOf(':');
        return colon < 0 ? "minecraft" : id.substring(0, colon);
    }

    private static String pathOf(String id) {
        int colon = id.indexOf(':');
        return colon < 0 ? id : id.substring(colon + 1);
    }

    public static Identifier parse(String id) {
        Identifier result = tryParse(id);
        if (result == null) throw new IdentifierException("Invalid identifier: " + id);
        return result;
    }

    public static Identifier tryParse(String id) {
        if (id == null) return null;
        int colon = id.indexOf(':');
        if (colon < 0) return tryCreate("minecraft", id);
        return tryCreate(id.substring(0, colon), id.substring(colon + 1));
    }

    public static Identifier withDefaultNamespace(String path) {
        return new Identifier("minecraft", path);
    }

    public static Identifier fromNamespaceAndPath(String namespace, String path) {
        return new Identifier(namespace, path);
    }

    public static Identifier read(com.mojang.brigadier.StringReader reader) {
        return Identifier.parse(reader.readUnquotedString());
    }


    public static Identifier readNonEmpty(StringReader reader) throws CommandSyntaxException {
        Identifier result = read(reader);
        if (result.getPath().isEmpty()) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
        return result;
    }

    private static Identifier tryCreate(String namespace, String path) {
        try { return new Identifier(namespace, path); }
        catch (Throwable t) { return null; }
    }

    private static boolean isAllowed(char c) {
        return c == ':' || c == '/' || c == '.' || c == '_' || c == '-' ||
            (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
    }
}
