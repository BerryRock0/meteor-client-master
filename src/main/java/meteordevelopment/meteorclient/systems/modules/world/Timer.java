/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;

public class Timer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> doubleValue = sgGeneral.add(new DoubleSetting.Builder()
        .name("value")
        .description("The timer value amount.")
        .defaultValue(1)
        .build()
    );
    
    private final Setting<Tick> mode = sgGeneral.add(new EnumSetting.Builder<Tick>()
        .name("Mode")
        .description("Timer ticks mode.")
        .defaultValue(Tick.Multiplication)
        .build()
    );

    public static final double OFF = 1;
    private double override = 1;

    public Timer() {
        super(Categories.World, "timer", "Changes the speed of everything in your game.");
    }
    
    public int setTick(int a)
    {
        if(isActive()) 
        switch (mode.get())
        {
            case Addition -> {a+= doubleValue.get();}
            case Subtraction -> {a-= doubleValue.get();}
            case Multiplication -> {a*= doubleValue.get();}
            case Division -> {a/= doubleValue.get();}
            case Exponentiation -> {a=Math.pow(a, doubleValue.get());}
        }
        else OFF;

        return (int) a;      
    }

    public void setOverride(double override) {
        this.override = override;
    }

    public enum Tick
    {
        Addition, 
        Subtraction,
        Multiplication,
        Division,
        Exponentiation
    }
}
