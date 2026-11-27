/* Compatibility shim for the 26.x player skin API. */
package net.minecraft.world.entity.player;

public final class PlayerSkin {
    private PlayerSkin() {}

    public static net.minecraft.resources.Identifier getInsecureSkinLocation(com.mojang.authlib.GameProfile profile) {
        return net.minecraft.resources.Identifier.parse("textures/entity/steve.png");
    }
}
