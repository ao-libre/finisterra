package ar.com.tamborindeguy.client.systems.interactions;

import ar.com.tamborindeguy.client.handlers.ObjectHandler;
import ar.com.tamborindeguy.client.screens.GameScreen;
import ar.com.tamborindeguy.objects.types.*;
import camera.Focused;
import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import entity.character.info.Inventory;

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
                if (equipped) {
                    entity.weaponIndex(((WeaponObj) obj).getAnimationId());
                } else {
                    entity.removeWeapon();
                }
            } else if (obj instanceof ArmorObj) {
                if (equipped) {

                    entity.bodyIndex(((ArmorObj) obj).getBodyNumber());
                } else {
//                    entity.removeBody(); should reset to naked body
                }
            } else if (obj instanceof HelmetObj) {
                if (equipped) {
                    entity.helmetIndex(((HelmetObj) obj).getAnimationId());
                } else {
                    entity.removeHelmet();
                }
            } else if (obj instanceof ShieldObj) {
                if (equipped) {
                    entity.shieldIndex(((ShieldObj) obj).getAnimationId());
                } else {
                    entity.removeShield();
                }
            }
        });
    }
}
