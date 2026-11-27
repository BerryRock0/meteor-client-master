package meteordevelopment.meteorclient.mixininterface;

/** Compatibility shim for the GL capability tracker interface. */
public interface ICapabilityTracker {
    boolean meteor$get();
    void meteor$set(boolean enabled);
}
