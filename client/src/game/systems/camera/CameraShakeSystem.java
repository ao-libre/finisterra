package game.systems.camera;

import com.artemis.annotations.All;
import com.artemis.annotations.Wire;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import component.camera.Focused;
import component.entity.character.status.Health;

import static com.artemis.E.E;

@All({Focused.class, Health.class})
@Wire
public class CameraShakeSystem extends IteratingSystem {

    private float shake = 0;
    private int hp = 0;
    private Vector2 push = new Vector2();
    private CameraSystem cameraSystem;

    @Override
    protected void inserted(int entityId) {
        hp = E(entityId).healthMin();
    }

    @Override
    protected void process(int entityId) {
        int actualHP = E(entityId).healthMin();
        if (actualHP < hp) {
            // add shake
            shake((hp - actualHP) / 10f);
            push(5, 5);

            // update field
            hp = actualHP;
        }
        if (shake != 0) {
            shake();
        }
    }

    public void shake(float pixels) {
        shake += pixels;
    }

    public void push(float x, float y) {
        push.x = x;
        push.y = y;
    }

    private void shake() {
        final OrthographicCamera camera = cameraSystem.camera;
        camera.position.x = (int) (camera.position.x + MathUtils.random(push.x) + (shake != 0 ? MathUtils.random(-shake, shake) : 0));
        camera.position.y = (int) (camera.position.y + MathUtils.random(push.y) + (shake != 0 ? MathUtils.random(-shake, shake) : 0));
        camera.update();

        if (shake > 0) {
            shake -= world.delta * 4f;
            if (shake < 0) shake = 0;
        }
        decrease(push, world.delta * 16f);
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
