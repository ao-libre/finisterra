package server.combat;

import com.artemis.Component;
import com.artemis.E;
import entity.character.states.Immobile;
import entity.character.status.Health;
import entity.character.status.Mana;
import entity.world.CombatMessage;
import entity.world.Dialog;
import physics.AttackAnimation;
import position.WorldPos;
import server.core.Server;
import server.manager.IManager;
import server.manager.WorldManager;
import shared.model.Spell;
import shared.network.combat.SpellCastRequest;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.FXNotification;
import shared.objects.types.HelmetObj;
import shared.objects.types.Obj;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static shared.util.Messages.*;

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
            AttackAnimation attackAnimation = new AttackAnimation();
            getServer().getWorldManager().notifyUpdate(userId, new EntityUpdate(userId, new Component[]{attackAnimation}, new Class[0]));
            castSpell(userId, target.get(), spell);
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
                .map(entity -> E(entity))
                .filter(Objects::nonNull)
                .filter(entity -> entity.hasWorldPos())
                .filter(entity -> entity.getWorldPos().equals(worldPos) || footprintOf(entity.id(), worldPos))
                .map(E::id)
                .findFirst();
    }

    private boolean footprintOf(Integer entity, WorldPos worldPos) {
        final Set<Integer> footprints = getServer().getMapManager().getEntitiesFootprints().get(entity);
        return footprints != null && footprints.stream().anyMatch(footprint -> worldPos.equals(E(footprint).getWorldPos()));
    }

    // TODO refactor what elements/components to send
    private void castSpell(int playerId, int target, Spell spell) {
        int requiredMana = spell.getRequiredMana();
        Mana mana = E(playerId).getMana();
        // TODO check stamina ?
        if (mana.min > requiredMana) {
            if (!isValid(target, spell)) {
                notifyInfo(playerId, INVALID_TARGET);
                return;
            }

            // add FX
            List<Component> toAdd = new ArrayList<>();
            List<Class> toRemove = new ArrayList<>();
            int fxGrh = spell.getFxGrh();
            E targetEntity = E(target);
            if (spell.getSumHP() > 0) {
                Health health = targetEntity.getHealth();
                int damage = calculateMagicDamage(playerId, target, spell);
                if (damage < 0 && target == playerId) {
                    notifyMagic(playerId, CANT_ATTACK_YOURSELF);
                    return;
                }
                health.min += damage;
                health.min = Math.max(0, health.min);
                final CombatMessage combatMessage = CombatMessage.magic(String.valueOf(damage));
                getWorldManager().notifyUpdate(target, new EntityUpdate(target, new Component[]{combatMessage}, new Class[0]));
                getWorldManager().sendEntityUpdate(target, new EntityUpdate(target, new Component[]{health}, new Class[0]));
                if (health.min == 0) {
                    getWorldManager().userDie(target);
                    if (playerId == target) { // TODO HACK
                        return;
                    }
                }
            }
            if (spell.isImmobilize()) {
                targetEntity.immobile();
                toAdd.add(targetEntity.getImmobile());
            } else if (spell.isRemoveParalysis()) {
                if (targetEntity.isImmobile()) {
                    targetEntity.immobile(false);
                    toRemove.add(Immobile.class);
                } else {
                    notifyInfo(playerId, NOT_PARALYSIS);
                    return;
                }
            }

            if (fxGrh > 0) {
                getWorldManager().notifyUpdate(target, new FXNotification(target, fxGrh - 1));
            }

            updateMana(playerId, requiredMana, mana);

            if (playerId == target) {
                notifyMagic(playerId, spell.getOwnerMsg());
            } else {
                notifyMagic(playerId, spell.getOriginMsg() + " " + getName(target));;
                notifyMagic(target, getName(playerId) + " " + spell.getTargetMsg());
            }

            getWorldManager().notifyUpdate(playerId, new EntityUpdate(playerId, new Component[]{new Dialog(spell.getMagicWords(), Dialog.Kind.MAGIC_WORDS)}, new Class[0]));
            getWorldManager().notifyUpdate(target, new EntityUpdate(target, toAdd.toArray(new Component[0]), toRemove.toArray(new Class[0])));
        } else {
            notifyInfo(playerId, NOT_ENOUGHT_MANA);
        }
    }

    private int calculateMagicDamage(int user, int target, Spell spell) {
        int damage = 0;
        final int minHP = spell.getMinHP();
        final int maxHP = spell.getMaxHP();
        damage = ThreadLocalRandom.current().nextInt(minHP, maxHP + 1);
        damage = damage * (3 * E(user).levelLevel()) / 100;
        if (spell.getSumHP() == 1) { // HEAL
            // TODO
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
        getWorldManager().sendEntityUpdate(playerId, new EntityUpdate(playerId, new Component[]{mana}, new Class[0]));
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

    private void notifyInfo(int userId, String message) {
        final ConsoleMessage combat = ConsoleMessage.info(message);
        getWorldManager().sendEntityUpdate(userId, combat);
    }

    private void notifyMagic(int userId, String message) {
        final ConsoleMessage combat = ConsoleMessage.combat(message);
        getWorldManager().sendEntityUpdate(userId, combat);
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

    private WorldManager getWorldManager() {
        return getServer().getWorldManager();
    }

}
