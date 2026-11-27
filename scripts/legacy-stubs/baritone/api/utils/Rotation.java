package baritone.api.utils;

/** Compile-only stand-in for Baritone's rotation. */
public class Rotation {
    public final float yaw, pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
