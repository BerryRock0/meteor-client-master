/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */
package meteordevelopment.meteorclient.mc;

import java.lang.reflect.Method;

/**
 * Version-agnostic facade over the 2D matrix stack exposed by {@code GuiGraphics}.
 *
 * <p>26.x and 1.21.6 expose {@code org.joml.Matrix3x2fStack} from {@code pose()}, while older
 * versions expose a {@code PoseStack} with 3-argument translate/scale. Shared code only needs
 * push/pop/translate/scale, so this facade bridges both shapes through reflection and keeps
 * {@code net.minecraft} types out of the common source root.</p>
 */
public interface PoseStack2D {
    static PoseStack2D of(Object graphics) {
        try {
            Object stack = graphics.getClass().getMethod("pose").invoke(graphics);
            return new Reflective(stack);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot access the GUI pose stack", e);
        }
    }

    void push();

    void pop();

    void scale(float x, float y);

    void scale(float scale);

    void translate(float x, float y);

    final class Reflective implements PoseStack2D {
        private final Object stack;
        private final Method push;
        private final Method pop;
        private final Method scale2;
        private final Method scale1;
        private final Method translate2;

        private Reflective(Object stack) {
            this.stack = stack;
            this.push = find(stack.getClass(), new String[]{"pushMatrix", "pushPose", "push"}, 0);
            this.pop = find(stack.getClass(), new String[]{"popMatrix", "popPose", "pop"}, 0);
            this.scale2 = find(stack.getClass(), new String[]{"scale"}, 2, 3);
            this.scale1 = find(stack.getClass(), new String[]{"scale"}, 1);
            this.translate2 = find(stack.getClass(), new String[]{"translate"}, 2, 3);
        }

        private static Method find(Class<?> type, String[] names, int... argCounts) {
            for (int count : argCounts) {
                for (String name : names) {
                    for (Method m : type.getMethods()) {
                        if (m.getName().equals(name) && m.getParameterCount() == count) {
                            m.setAccessible(true);
                            return m;
                        }
                    }
                }
            }
            throw new IllegalStateException("No method " + String.join("/", names) + " on " + type.getName());
        }

        private void invoke(Method method, Object... args) {
            try {
                method.invoke(stack, args);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot call " + method.getName(), e);
            }
        }

        private static Object numeric(Method method, int index, float value) {
            return method.getParameterTypes()[index] == double.class ? (double) value : value;
        }

        @Override public void push() { invoke(push); }

        @Override public void pop() { invoke(pop); }

        @Override public void scale(float x, float y) {
            if (scale2.getParameterCount() == 2) invoke(scale2, numeric(scale2, 0, x), numeric(scale2, 1, y));
            else invoke(scale2, numeric(scale2, 0, x), numeric(scale2, 1, y), numeric(scale2, 2, 1));
        }

        @Override public void scale(float scale) {
            invoke(scale1, numeric(scale1, 0, scale));
        }

        @Override public void translate(float x, float y) {
            if (translate2.getParameterCount() == 2) invoke(translate2, numeric(translate2, 0, x), numeric(translate2, 1, y));
            else invoke(translate2, numeric(translate2, 0, x), numeric(translate2, 1, y), numeric(translate2, 2, 0));
        }
    }
}
