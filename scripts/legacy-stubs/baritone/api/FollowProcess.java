package baritone.api;

import net.minecraft.entity.Entity;

import java.util.function.Predicate;

/** Compile-only stand-in for Baritone's follow process. */
public class FollowProcess {
    public void follow(Predicate<Entity> predicate) {}
}
