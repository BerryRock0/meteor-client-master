package net.minecraft.client.input;

public interface InputWithModifiers {
    int input();
    int modifiers();
    default boolean isSelection() { return false; }
    default boolean isConfirmation() { return false; }
    default boolean isEscape() { return false; }
    default boolean isLeft() { return false; }
    default boolean isRight() { return false; }
    default boolean isUp() { return false; }
    default boolean isDown() { return false; }
    default boolean isCycleFocus() { return false; }
    default int getDigit() { return -1; }
    default boolean hasAltDown() { return false; }
    default boolean hasShiftDown() { return false; }
    default boolean hasControlDown() { return false; }
    default boolean hasControlDownWithQuirk() { return false; }
    default boolean isSelectAll() { return false; }
    default boolean isCopy() { return false; }
    default boolean isPaste() { return false; }
    default boolean isCut() { return false; }
}
