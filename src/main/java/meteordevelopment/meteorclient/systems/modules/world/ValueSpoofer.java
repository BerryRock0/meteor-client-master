package meteordevelopment.meteorclient.systems.modules.world;

import org.joml.Vector3d;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;

public class ValueSpoofer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Double> distanceToEntity = sgGeneral.add(new DoubleSetting.Builder()
        .name("distance-to-entity")
        .description("Distance to entities value")
        .build()
    );

   public final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Select specific entities.")
        .build()
    );

    private final Setting<Boolean> cases = sgBounds.add(new BoolSetting.Builder()
        .name("cases")
        .description("Disables/enables black/white cases.")
        .defaultValue(false)
        .build()
    );

    public ValueSpoofer() {
        super(Categories.World, "valuespoofer", "Spoofs values.");
    }
    
    public boolean inList(Entity entity)
    {
        return isActive() && inFrames(entity);
    }

    public boolean inFrames(Entity entity)
    {
        switch(filter.get())
        {
            case White -> {return entities.get().contains(entity.getType());}
            case Black -> {return !entities.get().contains(entity.getType());}
        }
        return cases.get();
    }

    public enum Filter
    {
        White,
        Black
    }
}
