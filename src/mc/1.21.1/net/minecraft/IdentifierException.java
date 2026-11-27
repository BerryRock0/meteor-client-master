/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package net.minecraft;

/** Compatibility shim: 26.x renamed {@code ResourceLocationException} to {@code IdentifierException}. */
public class IdentifierException extends ResourceLocationException {
    public IdentifierException(String message) { super(message); }
    public IdentifierException(String message, Throwable cause) { super(message, cause); }
}
