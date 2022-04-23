package game.systems.profiling;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.esotericsoftware.minlog.Log;

public class ProfilerSystem extends BaseSystem {

    private final GLProfiler profiler;

    public ProfilerSystem() {
        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
    }

    @Override
    protected void processSystem() {
        printDetails();
        profiler.reset();
    }

    private void printDetails() {
//        Log.info("Profiling info: ");
//        Log.info("  Calls: " + profiler.getCalls());
//        Log.info("  Draw Calls: " + profiler.getDrawCalls());
//        Log.info("  Texture Bindings: " + profiler.getTextureBindings());
//        Log.info("  Shader Switches: " + profiler.getShaderSwitches());
//        Log.info("  Vertex Count: " + profiler.getVertexCount().value);
//        Log.info("----------------");
    }

}
