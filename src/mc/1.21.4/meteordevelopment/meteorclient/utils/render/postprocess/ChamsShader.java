/* Compatibility stub for the 26.x chams post-process API. */
package meteordevelopment.meteorclient.utils.render.postprocess;

public class ChamsShader extends EntityShader {
    public ChamsShader() { super(null); }

    @Override
    protected boolean shouldDraw() { return false; }
}
