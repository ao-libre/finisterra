package ar.com.tamborindeguy.client.systems.render.world;

import ar.com.tamborindeguy.client.systems.camera.CameraSystem;
import com.artemis.Aspect;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.esotericsoftware.spine.*;
import entity.Body;
import entity.Heading;
import entity.character.Character;
import position.WorldPos;

@Wire
public class AnimationRenderingSystem extends IteratingSystem {

    final SkeletonMeshRenderer renderer = new SkeletonMeshRenderer();
    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;
    private CameraSystem cameraSystem;
    private PolygonSpriteBatch batch;
//    SkeletonRendererDebug debugRenderer;


    public AnimationRenderingSystem(PolygonSpriteBatch batch) {
        super(Aspect.all(Character.class, WorldPos.class, Body.class, Heading.class));
        this.batch = batch;
        atlas = new TextureAtlas(Gdx.files.internal("data/animations/human.atlas"));
        SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("data/animations/human.json"));
        skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
        skeleton.setPosition(32, 0);
        AnimationStateData stateData = new AnimationStateData(skeletonData);
        state = new AnimationState(stateData);
        state.setTimeScale(2f);
        renderer.setPremultipliedAlpha(true);
    }

    @Override
    protected void begin() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cameraSystem.guiCamera.update();
        batch.setProjectionMatrix(cameraSystem.guiCamera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(int entityId) {
        state.update(Gdx.graphics.getDeltaTime()); // Update the animation time.
        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
        renderer.draw(batch, skeleton); // Draw the skeleton images.
    }
}
