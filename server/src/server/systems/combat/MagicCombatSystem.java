package server.systems.combat;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import component.console.ConsoleMessage;
import component.entity.character.attributes.Attribute;
import component.entity.character.states.Buff;
import component.entity.character.states.Immobile;
import component.entity.character.status.Health;
import component.entity.character.status.Mana;
import component.entity.character.status.Stamina;
import component.entity.world.CombatMessage;
import component.entity.world.Dialog;
import component.physics.AttackAnimation;
import component.position.WorldPos;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.CharacterTrainingSystem;
import server.systems.entity.EffectEntitySystem;
import server.systems.entity.SoundEntitySystem;
import server.systems.manager.MapManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.network.UpdateTo;
import shared.model.Spell;
import shared.network.combat.SpellCastRequest;
import shared.network.notifications.EntityUpdate;
import shared.objects.types.HelmetObj;
import shared.objects.types.Obj;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;

@Wire
public class MagicCombatSystem extends PassiveSystem {

    private static final String SPACE = " ";
    private static final int TIME_TO_MOVE_1_TILE = 200;

    // Injected Systems
    private MapManager mapManager;
    private WorldManager worldManager;
    private ObjectManager objectManager;
    private CharacterTrainingSystem characterTrainingSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private EffectEntitySystem effectEntitySystem;
    private MessageSystem messageSystem;
    private SoundEntitySystem soundEntitySystem;

    public void spell(int userId, SpellCastRequest spellCastRequest) {
        final Spell spell = spellCastRequest.getSpell();
        final WorldPos targetPos = spellCastRequest.getWorldPos();
        final long timestamp = spellCastRequest.getTimestamp();
        Optional<Integer> target = getTarget(userId, targetPos, timestamp);
        if (target.isPresent()) {
            EntityUpdate update = EntityUpdateBuilder.of(userId).withComponents(new AttackAnimation()).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);

            castSpell(userId, target.get(), spell);
        }
    }

    private Optional<Integer> getTarget(int userId, WorldPos worldPos, long timestamp) {
        Set<Integer> entities = new HashSet<>(mapManager.getNearEntities(userId));
        entities.add(userId);
        return entities
                .stream()
                .map(E::E)
                .filter(Objects::nonNull)
                .filter(E::hasWorldPos)
                .filter(e -> !e.hasObject())
                .filter(entity -> isValidTarget(worldPos, timestamp, entity))
                .map(E::id)
                .findFirst();
    }

    private boolean isValidTarget(WorldPos worldPos, long timestamp, E entity) {
        return entity.getWorldPos().equals(worldPos) || footprintOf(entity.id(), worldPos, timestamp);
    }

    private boolean footprintOf(Integer entity, WorldPos worldPos, long timestamp) {
        final Set<Integer> footprints = mapManager.getEntitiesFootprints().get(entity);
        return footprints != null && footprints
                .stream()
                .anyMatch(footprint -> worldPos.equals(E(footprint).getWorldPos()) && (timestamp - E(footprint).getFootprint().timestamp <= TIME_TO_MOVE_1_TILE));
    }

    // TODO refactor what elements/components to send
    private void castSpell(int playerId, int target, Spell spell) {
        int requiredMana = spell.getRequiredMana();
        int requiredStamina = spell.getRequiredStamina();
        Mana mana = E(playerId).getMana();
        Stamina stamina = E(playerId).getStamina();

        EntityUpdateBuilder playerUpdateBuilder = EntityUpdateBuilder.of(playerId);
        EntityUpdateBuilder victimUpdateBuilder = EntityUpdateBuilder.of(target);
        EntityUpdateBuilder victimUpdateToAllBuilder = EntityUpdateBuilder.of(target);

        if (mana.min > requiredMana) {
            if (!isValid(target, spell)) {
                notifyInfo(playerId, Messages.INVALID_TARGET);
                return;
            }

            if (stamina.min < requiredStamina) {
                notifyInfo(playerId, Messages.NOT_ENOUGH_ENERGY);
                return;
            }

            if (playerId == target) {
                if (spell.getSumHP() == 2 || spell.isImmobilize() || spell.isParalyze()) {
                    notifyMagic(playerId, Messages.CANT_ATTACK_YOURSELF);
                    return;
                }
                notifyMagic(playerId, Messages.OWNER_MSG, spell.getOwnerMsg());
            } else {
                notifyMagic(playerId, Messages.ORIGIN_MSG, spell.getOriginMsg(), getName(target));
                notifyMagic(target, Messages.TARGET_MSG, getName(playerId), spell.getTargetMsg());
            }

            int fxGrh = spell.getFxGrh();
            E targetEntity = E(target);
            int damage;
            if (spell.getSumHP() > 0) {
                Health health = targetEntity.getHealth();
                damage = calculateMagicDamage(playerId, target, spell);
                health.min += damage;
                health.min = Math.max(0, health.min);
                victimUpdateToAllBuilder.withComponents(CombatMessage.magic(damage > 0 ? "+" : "-" + Math.abs(damage)));
                victimUpdateBuilder.withComponents(health);
                if (damage > 0) {
                    notifyMagic(playerId, Messages.HEAL_TO, getName(target), Integer.toString(Math.abs(damage)));
                    notifyMagic(target, Messages.HEAL_BY, getName(playerId), Integer.toString(Math.abs(damage)));
                } else {
                    notifyMagic(playerId, Messages.DAMAGE_TO, Integer.toString(Math.abs(damage)), getName(target));
                    notifyMagic(target, Messages.DAMAGED_BY, getName(playerId), Integer.toString(Math.abs(damage)));
                }
                if (health.min <= 0) {
                    worldManager.entityDie(target);
                    notifyMagic(playerId, Messages.KILL, getName(target));
                    notifyMagic(target, Messages.KILLED, getName(playerId));
                    soundEntitySystem.add(playerId, 126);
                }
            }
            if (spell.isImmobilize()) {
                targetEntity.immobile();
                victimUpdateToAllBuilder.withComponents(targetEntity.getImmobile());
            } else if (spell.isRemoveParalysis()) {
                if (targetEntity.isImmobile()) {
                    targetEntity.immobile(false);
                    victimUpdateToAllBuilder.remove(Immobile.class);
                } else {
                    notifyInfo(playerId, Messages.NOT_PARALYSIS);
                    return;
                }
            }

            if (spell.isSumStrength()) {
                int random = new Random().nextInt(spell.getMaxStrength() - spell.getMinStrength() + 1) + spell.getMinStrength();
                targetEntity.strengthCurrentValue(targetEntity.strengthCurrentValue() + random);
                targetEntity.buff().buffAddAttribute(targetEntity.getStrength(), spell.getStrengthDuration());
                sendAttributeUpdate(target, targetEntity.getStrength(), targetEntity.getBuff());
            }

            if (spell.isSumAgility()) {
                int random = new Random().nextInt(spell.getMaxAgility() - spell.getMinAgility() + 1) + spell.getMinAgility();
                targetEntity.agilityCurrentValue(targetEntity.agilityCurrentValue() + random);
                targetEntity.buff().buffAddAttribute(targetEntity.getAgility(), spell.getAgilityDuration());
                sendAttributeUpdate(target, targetEntity.getAgility(), targetEntity.getBuff());
            }

            if (fxGrh > 0) {
                effectEntitySystem.addFX(target, fxGrh, Math.max(1, spell.getLoops()));
            }

            stamina.min -= requiredStamina;
            playerUpdateBuilder.withComponents(stamina);

            updateMana(playerId, requiredMana, mana);
            Dialog magicWords = new Dialog(spell.getMagicWords(), Dialog.Kind.MAGIC_WORDS);

            Log.info("Magic attack " + spell.getMagicWords());
            int spellSound = spell.getWav();
            soundEntitySystem.add(playerId, spellSound);

            EntityUpdate victimUpdate = victimUpdateBuilder.build();
            entityUpdateSystem.add(victimUpdate, UpdateTo.ALL);

            EntityUpdate playerUpdate = playerUpdateBuilder.withComponents(magicWords).build();
            entityUpdateSystem.add(playerUpdate, UpdateTo.ALL);
        } else {
            notifyInfo(playerId, Messages.NOT_ENOUGHT_MANA);
        }
    }

    private int calculateMagicDamage(int user, int target, Spell spell) {
        int damage;
        final int minHP = spell.getMinHP();
        final int maxHP = spell.getMaxHP();
        damage = ThreadLocalRandom.current().nextInt(minHP, maxHP + 1);
        damage = E(user).levelLevel() + damage;
        characterTrainingSystem.userTakeDamage(user, target, damage);
        if (spell.getSumHP() == 1) { // HEAL
            // TODO
        } else if (spell.getSumHP() == 2) {
            if (E(target).hasHelmet()) {
                final Optional<Obj> obj = objectManager.getObject(E(target).getHelmet().index);
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
        return damage;
    }

    private void updateMana(int playerId, int requiredMana, Mana mana) {
        mana.min -= requiredMana;
        // update mana
        EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(mana).build();
        entityUpdateSystem.add(update, UpdateTo.ENTITY);
    }

    private void sendAttributeUpdate(int player, Attribute attribute, Buff buff) {
        EntityUpdate updateAGI = EntityUpdateBuilder.of(E(player).id()).withComponents(attribute, buff).build();
        entityUpdateSystem.add(updateAGI, UpdateTo.ENTITY);
    }

    private boolean isValid(int target, Spell spell) {
        E targetEntity = E(target);
        int spellTarget = spell.getTarget();
        switch (spellTarget) {
            case 1:
            case 3:
                return targetEntity.isCharacter() || (targetEntity.hasNPC() && targetEntity.isHostile());
            case 2:
                return (targetEntity.hasNPC() && targetEntity.isHostile());
            case 4:
                return targetEntity == null;
        }

        return false;
    }

    private void notifyInfo(int userId, Messages messageId, String... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.info(messageId.name(), messageParams);
        messageSystem.add(userId, combat);
    }

    private void notifyMagic(int userId, Messages messageId, String... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.combat(messageId.name(), messageParams);
        messageSystem.add(userId, combat);
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

}
