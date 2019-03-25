package server.manager;

import server.network.NetworkComunicator;
import server.database.ServerDescriptorReader;
import shared.model.Spell;
import shared.model.readers.DescriptorsReader;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import com.artemis.Component;
import com.artemis.E;
import com.esotericsoftware.minlog.Log;
import entity.CombatMessage;
import entity.Dialog;
import entity.character.states.Immobile;
import entity.character.status.Health;
import entity.character.status.Mana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.artemis.E.E;

/**
 * Spell Logic
 */
public class SpellManager {
    private static DescriptorsReader reader = new ServerDescriptorReader();
    private static Map<Integer, Spell> spells;

    public static void load() {
        Log.info("Loading spells...");
        spells = reader.loadSpells("hechizos");
    }

    public static Optional<Spell> getSpell(int id) {
        return Optional.ofNullable(spells.get(id));
    }

    public static void castSpell(int playerId, int target, Spell spell) {
        int requiredMana = spell.getRequiredMana();
        Mana mana = E(playerId).getMana();
        // TODO check stamina
        if (mana.min > requiredMana) {
            if (!isValid(target, spell)) {
                // TODO notify not valid target
            }
            mana.min -= requiredMana;
            // update mana
            NetworkComunicator.sendTo(NetworkComunicator.getConnectionByPlayer(playerId), new EntityUpdate(playerId, new Component[]{mana}, new Class[0]));
            // add FX
            List<Component> toAdd = new ArrayList<>();
            List<Class> toRemove = new ArrayList<>();
            int fxGrh = spell.getFxGrh();
            if (fxGrh > 0) {
                WorldManager.notifyUpdate(target, new FXNotification(target, fxGrh - 1));
            }
            E targetEntity = E(target);
            if (spell.getSumHP() > 0) {
                Health health = targetEntity.getHealth();
                int damage = CombatManager.calculateMagicDamage(target, spell);
                health.min += damage;
                CombatManager.notify(target, new CombatMessage(String.valueOf(damage)));
                CombatManager.update(target);
            }
            if (spell.isImmobilize()) {
                targetEntity.immobile();
                toAdd.add(targetEntity.getImmobile());
            } else if (spell.isRemoveParalysis() && targetEntity.isImmobile()) {
                targetEntity.immobile(false);
                toRemove.add(Immobile.class);
            }
            WorldManager.notifyUpdate(playerId, new EntityUpdate(playerId, new Component[]{new Dialog(spell.getMagicWords(), Dialog.Kind.MAGIC_WORDS)}, new Class[0]));
            WorldManager.notifyUpdate(target, new EntityUpdate(target, toAdd.toArray(new Component[0]), toRemove.toArray(new Class[0])));
        } else {
            // TODO notify no mana
        }
    }

    private static boolean isValid(int target, Spell spell) {
        E targetEntity = E(target);
        int spellTarget = spell.getTarget();
        switch (spellTarget) {
            case 1:
                return targetEntity.isCharacter();
//            case 2:
//                return targetEntity.isNPC();
            case 3:
                return targetEntity.isCharacter();
        }

        return false;
    }

}
