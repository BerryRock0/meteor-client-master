package baritone.api.process;

/** Compile-only stand-in for Baritone's process interface. */
public interface IBaritoneProcess {
    boolean isActive();

    PathingCommand onTick(boolean safe, boolean dangerous);

    boolean isTemporary();

    void onLostControl();

    double priority();

    String displayName0();
}
