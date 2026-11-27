package net.fabricmc.fabric.api.client.keybinding;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

/** Compile-only replacement for the old Fabric keybinding API. */
public class FabricKeyBinding extends KeyBinding {
    public static final class Builder {
        private final Identifier id;
        private InputUtil.Type type;
        private int code;
        private String category;

        private Builder(Identifier id) {
            this.id = id;
        }

        public static Builder create(Identifier id, InputUtil.Type type, int code, String category) {
            Builder builder = new Builder(id);
            builder.type = type;
            builder.code = code;
            builder.category = category;
            return builder;
        }

        public FabricKeyBinding build() {
            return new FabricKeyBinding(id.getPath(), type, code, category);
        }
    }

    public FabricKeyBinding(String translationKey, InputUtil.Type type, int code, String category) {
        super(translationKey, type, code, category);
    }

    public InputUtil.Key getBoundKey() {
        return null;
    }
}
