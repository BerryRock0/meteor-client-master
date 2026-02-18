package meteordevelopment.meteorclient.systems.modules.world;

import org.joml.Vector3d;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;

public class Telekinesis extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgBounds = settings.createGroup("Bounds");
    private final SettingGroup sgFunctions = settings.createGroup("Functions");
    
   public final Setting<Vector3d> velocity = sgGeneral.add(new Vector3dSetting.Builder()
        .name("velocity")
        .description("Entites velocity.")
        .defaultValue(0, 0, 0)
        .build()
    );

   public final Setting<Double> yaw = sgGeneral.add(new DoubleSetting.Builder()
        .name("yaw")
        .description("Entity yaw value.")
        .defaultValue(0)
        .sliderMin(-180)
        .sliderMax(180)
        .build()
    );

   public final Setting<Double> pitch = sgGeneral.add(new DoubleSetting.Builder()
        .name("pitch")
        .description("Entity pitch value.")
        .defaultValue(0)
        .sliderMin(-180)
        .sliderMax(180)
        .build()
    );

   public final Setting<Set<EntityType<?>>> entities = sgBounds.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Select specific entities.")
        .build()
    );

    public final Setting<Boolean> cases = sgBounds.add(new BoolSetting.Builder()
        .name("list-final-boolean")
        .description("Switches black/white cases.")
        .defaultValue(false)
        .build()
    );
    
    public final Setting<Boolean> entitylist = sgBounds.add(new BoolSetting.Builder()
        .name("entitylist-case-boolean")
        .description("Switches black/white list.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> uuidlist = sgBounds.add(new BoolSetting.Builder()
        .name("uuidlist-case-boolean")
        .description("Switches black/white list.")
        .defaultValue(false)
        .build()
    );

    private final Setting<List<String>> uuids = sgBounds.add(new StringListSetting.Builder()
        .name("uuids")
        .description("setting commands")
        .build()
    );

    public final Setting<Boolean> axis = sgFunctions.add(new BoolSetting.Builder()
        .name("axis-move")
        .description("Manipulate entity speeds.")
        .defaultValue(false)
        .build()
    );
    public final Setting<Boolean> angle = sgFunctions.add(new BoolSetting.Builder()
        .name("angle-move")
        .description("Change entity angles.")
        .defaultValue(false)
        .build()
    );
    
    public Telekinesis()
    {
        super(Categories.World, "telekinesis", "Move entities in third axis.");
    }

    public boolean task(Entity entity)
    {
        return isActive() && inList(entity);
    }

    public boolean inList(Entity entity)
    {
        if (uuids.get().contains(entity.getUuid().toString()))
            return uuidlist.get();
        
        if (entities.get().contains(entity.getType()))
            return entitylist.get();
        
        return cases.get();
    }
}
