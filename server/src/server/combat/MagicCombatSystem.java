package server.combat;

import com.artemis.Component;
import com.artemis.E;
import com.artemis.World;
import entity.character.states.Immobile;
import entity.character.status.Health;
import entity.character.status.Mana;
import entity.world.CombatMessage;
import entity.world.Dialog;
import physics.AttackAnimation;
import position.WorldPos;
import server.core.Server;
import server.manager.CombatManager;
import server.manager.IManager;
import server.manager.ObjectManager;
import shared.model.Spell;
import shared.network.combat.SpellCastRequest;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.objects.types.HelmetObj;
import shared.objects.types.Obj;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

public class MagicCombatSystem implements IManager {

    private Server server;

    public MagicCombatSystem(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public void spell(int userId, SpellCastRequest spellCastRequest) {
        final Spell spell = spellCastRequest.getSpell();
        final WorldPos targetPos = spellCastRequest.getWorldPos();
        final long timestamp = spellCastRequest.getTimestamp();
        Optional<Integer> target = getTarget(userId, targetPos, timestamp);
        if (target.isPresent()) {
            castSpell(userId, target.get(), spell);
            AttackAnimation attackAnimation = new AttackAnimation();
            getServer().getWorldManager().notifyUpdate(userId, new EntityUpdate(userId, new Component[]{attackAnimation}, new Class[0]));
        } else {
            // TODO
//            List<WorldPos> area = getArea(worldPos, 3);
//            int fxGrh = spell.getFxGrh();
//            if (fxGrh > 0) {
//                area.forEach(pos -> {
//                    World world = getServer().getWorld();
//                    int entity = world.create();
//                    // TODO notify all near users instead of playerid
//                    getServer().getWorldManager().notifyUpdate(userId, new EntityUpdate(entity, new Component[]{pos, new Ground()}, new Class[0]));
//                    getServer().getWorldManager().notifyUpdate(userId, new FXNotification(entity, fxGrh - 1));
//                    world.delete(entity);
//                });
//            }
        }
    }

    private Optional<Integer> getTarget(int userId, WorldPos worldPos, long timestamp) {
        Set<Integer> entities = new HashSet<>(getServer().getMapManager().getNearEntities(userId));
        entities.add(userId);
        return entities
                .stream()
                .filter(entity -> E(entity).getWorldPos().equals(worldPos) || footprintOf(entity, worldPos))
                .findFirst();
    }

    private boolean footprintOf(Integer entity, WorldPos worldPos) {
        final Set<Integer> footprints = getServer().getMapManager().getEntitiesFootprints().get(entity);
        return footprints != null && footprints.stream().anyMatch(footprint -> worldPos.equals(E(footprint).getWorldPos()));
    }


    private void castSpell(int playerId, int target, Spell spell) {
        int requiredMana = spell.getRequiredMana();
        Mana mana = E(playerId).getMana();
        // TODO check stamina ?
        if (mana.min > requiredMana) {
            if (!isValid(target, spell)) {
                // TODO notify not valid target
                return;
            }
            updateMana(playerId, requiredMana, mana);
            // add FX
            List<Component> toAdd = new ArrayList<>();
            List<Class> toRemove = new ArrayList<>();
            int fxGrh = spell.getFxGrh();
            if (fxGrh > 0) {
                getServer().getWorldManager().notifyUpdate(target, new FXNotification(target, fxGrh - 1));
            }
            E targetEntity = E(target);
            if (spell.getSumHP() > 0) {
                Health health = targetEntity.getHealth();
                int damage = calculateMagicDamage(playerId, target, spell);
                health.min += damage;
                final CombatMessage combatMessage = new CombatMessage(String.valueOf(damage));
                getServer().getWorldManager().notifyUpdate(target, new EntityUpdate(target, new Component[]{combatMessage}, new Class[0]));
                getServer().getWorldManager().sendEntityUpdate(target, new EntityUpdate(target, new Component[]{health}, new Class[0]));
            }
            if (spell.isImmobilize()) {
                targetEntity.immobile();
                toAdd.add(targetEntity.getImmobile());
            } else if (spell.isRemoveParalysis() && targetEntity.isImmobile()) {
                targetEntity.immobile(false);
                toRemove.add(Immobile.class);
            }
            getServer().getWorldManager().notifyUpdate(playerId, new EntityUpdate(playerId, new Component[]{new Dialog(spell.getMagicWords(), Dialog.Kind.MAGIC_WORDS)}, new Class[0]));
            getServer().getWorldManager().notifyUpdate(target, new EntityUpdate(target, toAdd.toArray(new Component[0]), toRemove.toArray(new Class[0])));
        } else {
            // TODO notify no mana
        }
    }

    private int calculateMagicDamage(int user, int target, Spell spell) {
        int damage = 0;
        final int minHP = spell.getMinHP();
        final int maxHP = spell.getMaxHP();
        damage = ThreadLocalRandom.current().nextInt(minHP, maxHP + 1);
        damage = damage * (3 * E(user).levelLevel()) / 100;
        if (spell.getSumHP() == 1) { // HEAL
        } else if (spell.getSumHP() == 2) {
            int magicDefense;
            if (E(target).hasHelmet()) {
                final Optional<Obj> obj = getServer().getObjectManager().getObject(E(target).getHelmet().index);
                obj
                        .filter(HelmetObj.class::isInstance)
                        .map(HelmetObj.class::cast)
                        .ifPresent(helmet -> {
                            // TODO Magic def
                        });
            }
            // TODO anillos
            damage = -damage;
        }
        return (int) (damage * 1.65f);
    }

    private void updateMana(int playerId, int requiredMana, Mana mana) {
        mana.min -= requiredMana;
        // update mana
        getServer().getWorldManager().sendEntityUpdate(playerId, new EntityUpdate(playerId, new Component[]{mana}, new Class[0]));
    }

    private boolean isValid(int target, Spell spell) {
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
