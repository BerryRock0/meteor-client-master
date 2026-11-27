package baritone.utils.accessor;

/** Compile-only stand-in for Baritone's chunk array accessor. */
public interface IChunkArray {
    int viewDistance();

    void copyFrom(IChunkArray other);
}
