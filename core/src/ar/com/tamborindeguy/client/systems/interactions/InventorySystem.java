package ar.com.tamborindeguy.client.systems.interactions;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.objects.types.ArmorObj;
import ar.com.tamborindeguy.objects.types.HelmetObj;
import ar.com.tamborindeguy.objects.types.Obj;
import ar.com.tamborindeguy.objects.types.WeaponObj;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import entity.character.info.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.artemis.E.E;

public class InventorySystem extends IteratingSystem {

    public InventorySystem(Window inventory) {
        super(Aspect.all(Focused.class, Inventory.class));
    }

    @Override
    protected void process(int entityId) {
    }

    // notify server instead of doing here
    public static void equip(int objId, boolean equipped) {
        Optional<Obj> object = ObjectHandler.getObject(objId);
        object.ifPresent(obj -> {
            int playerId = GameScreen.getPlayer();
            E entity = E(playerId);
            if (obj instanceof WeaponObj) {
                entity.weaponIndex(((WeaponObj) obj).getAnimationId());
            } else if (obj instanceof ArmorObj) {
                entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
            } else if (obj instanceof HelmetObj) {
                int animationId = ((HelmetObj) obj).getAnimationId();
                if (animationId != 0) {
                    entity.helmetIndex(animationId);
                }
            }
        });
    }
}
