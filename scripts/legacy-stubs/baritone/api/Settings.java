package baritone.api;

/** Compile-only stand-in for Baritone's settings object used by legacy snapshots. */
public class Settings {
    public static class Setting<T> {
        public final String name;
        public final T defaultValue;
        public T value;

        public Setting(String name, T defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }

        public Setting(T value) {
            this("setting", value);
        }

        public String getName() {
            return name;
        }
    }

    public final Setting<String> prefix = new Setting<>("prefix", "b");
    public final Setting<Boolean> assumeStep = new Setting<>("assumeStep", false);
    public final Setting<Boolean> assumeWalkOnWater = new Setting<>("assumeWalkOnWater", false);
    public final Setting<Boolean> assumeWalkOnLava = new Setting<>("assumeWalkOnLava", false);
    public final Setting<Boolean> mineScanDroppedItems = new Setting<>("mineScanDroppedItems", false);
    public final Setting<Integer> maxFallHeightNoWater = new Setting<>("maxFallHeightNoWater", 3);
    public final Setting<Double> costHeuristic = new Setting<>("costHeuristic", 1.0);
}
