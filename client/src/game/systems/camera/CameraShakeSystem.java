package game.systems.camera;

import com.artemis.BaseSystem;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;


@Wire
public class CameraShakeSystem extends BaseSystem {


    public CameraSystem cameraSystem;
    public float shake;
    public Vector2 push = new Vector2();

    public CameraShakeSystem() {
        shake = 0;
    }

    public void shake(float pixels) {
        shake += pixels;
    }

    public void push(float x, float y) {
        push.x = x;
        push.y = y;
    }

    @Override
    protected void processSystem() {
        final OrthographicCamera camera = cameraSystem.camera;
        if (shake != 0) {
            camera.position.x = (int) (camera.position.x + MathUtils.random(push.x) + (shake != 0 ? MathUtils.random(-shake, shake) : 0));
            camera.position.y = (int) (camera.position.y + MathUtils.random(push.y) + (shake != 0 ? MathUtils.random(-shake, shake) : 0));
            camera.update();

            if (shake > 0) {
                shake -= world.delta * 4f;
                if (shake < 0) shake = 0;
            }
            decrease(push, world.delta * 16f);
        }
    }

    private void decrease(final Vector2 v, final float delta) {
        if (v.x > 0) {
            v.x -= delta;
            if (v.x < 0) {
                v.x = 0;
            }
        }
        if (v.x < 0) {
            v.x += delta;
            if (v.x > 0) {
                v.x = 0;
            }
        }
        if (v.y > 0) {
            v.y -= delta;
            if (v.y < 0) {
                v.y = 0;
            }
        }
        if (v.y < 0) {
            v.y += delta;
            if (v.y > 0) {
                v.y = 0;
            }
        }
    }
}
