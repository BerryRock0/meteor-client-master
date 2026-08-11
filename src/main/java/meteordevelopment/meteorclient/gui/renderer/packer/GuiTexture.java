/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.gui.renderer.packer;

import java.util.ArrayList;
import java.util.List;

public class GuiTexture {
    private final List<TextureRegion> regions = new ArrayList<>(2);

    void add(TextureRegion region)
    {   
        regions.add(region);
    }

    public TextureRegion get(double width, double height)
    {
        double targetDiagonal = Math.hypot(width, height);
        return regions.stream().min((a, b) -> Double.compare(Math.abs(targetDiagonal - a.diagonal), Math.abs(targetDiagonal - b.diagonal))).orElse(null);
    }
}
