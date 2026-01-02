package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
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

    public ValueSpoofer() {
        super(Categories.World, "valuespoofer", "Spoofs values.");
    }
}
