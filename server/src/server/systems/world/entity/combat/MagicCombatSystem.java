package server.systems.world.entity.combat;

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
import server.systems.config.ObjectSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.world.MapSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.factory.EffectEntitySystem;
import server.systems.world.entity.factory.SoundEntitySystem;
import server.systems.world.entity.training.CharacterTrainingSystem;
import server.utils.UpdateTo;
import shared.model.Spell;
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
    private MapSystem mapSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private ObjectSystem objectSystem;
    private CharacterTrainingSystem characterTrainingSystem;
    private EntityUpdateSystem entityUpdateSystem;
    private EffectEntitySystem effectEntitySystem;
    private MessageSystem messageSystem;
    private SoundEntitySystem soundEntitySystem;

    public void spell(int userId, Spell spell, WorldPos targetPos, long timestamp) {
        Optional<Integer> target = getTarget(userId, targetPos, timestamp);
        if (target.isPresent()) {
            EntityUpdate update = EntityUpdateBuilder.of(userId).withComponents(new AttackAnimation()).build();
            entityUpdateSystem.add(update, UpdateTo.ALL);

            castSpell(userId, target.get(), spell);
        }
    }

    private Optional<Integer> getTarget(int userId, WorldPos worldPos, long timestamp) {
        Set<Integer> entities = new HashSet<>(mapSystem.getNearEntities(userId));
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
        final Set<Integer> footprints = mapSystem.getEntitiesFootprints().get(entity);
        return footprints != null && footprints
                .stream()
                .anyMatch(footprint -> worldPos.equals(E(footprint).getWorldPos()) && (timestamp - E(footprint).getFootprint().timestamp <= TIME_TO_MOVE_1_TILE));
    }

    // TODO refactor what elements/components to send
    // @todo verificar por target muerto
    // @todo implementar hechizo resucitar
    // @todo usar enums donde corresponda para poder saber qué significan las constantes numéricas
    private void castSpell(int playerId, int target, Spell spell) {
        int requiredMana = spell.getRequiredMana();
        int requiredStamina = spell.getRequiredStamina();
        Mana mana = E(playerId).getMana();
        Stamina stamina = E(playerId).getStamina();

        EntityUpdateBuilder playerUpdateBuilder = EntityUpdateBuilder.of(playerId);
        EntityUpdateBuilder victimUpdateBuilder = EntityUpdateBuilder.of(target);
        EntityUpdateBuilder victimUpdateToAllBuilder = EntityUpdateBuilder.of(target);

        if (E(playerId).healthMin() != 0) {
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
                    if(spell.getSumHP() == 2 || spell.isImmobilize() || spell.isParalyze()) {
                        notifyMagic(playerId, Messages.CANT_ATTACK_YOURSELF);
                        return;
                    }
                    if(spell.isSumStrength() || spell.isSumAgility()){
                        notifyMagic(playerId, Messages.OWNER_MSG, spell.getOwnerMsg());
                    }
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
                    // @todo si damage = 0 entonces falla el hechizo ¿?

                    health.min += damage;
                    health.min = Math.min(health.min, health.max);
                    health.min = Math.max(health.min, 0);

                    victimUpdateToAllBuilder.withComponents(CombatMessage.magic(damage > 0 ? "+" : "-" + Math.abs(damage)));
                    victimUpdateBuilder.withComponents(health);

                    if (damage > 0) {
                        if (playerId == target) {
                            notifyMagic(playerId, Messages.HEAL_SELF, getName(target), Integer.toString(Math.abs(damage)));
                        } else {
                            notifyMagic(playerId, Messages.HEAL_TO, getName(target), Integer.toString(Math.abs(damage)));
                            notifyMagic(target, Messages.HEAL_BY, getName(playerId), Integer.toString(Math.abs(damage)));
                        }
                    }

                    if (health.min <= 0) {
                        worldEntitiesSystem.entityDie(target);
                        notifyMagic(playerId, Messages.KILL, getName(target));
                        notifyMagic(target, Messages.KILLED, getName(playerId));
                        soundEntitySystem.add(playerId, 171);
                    }

                    if (fxGrh > 0) {
                        effectEntitySystem.addFX(target, fxGrh, Math.max(1, spell.getLoops()));
                    }

                    stamina.min -= requiredStamina;
                    playerUpdateBuilder.withComponents(stamina);

                    updateMana(playerId, requiredMana, mana);

                    Dialog magicWords = new Dialog(spell.getMagicWords(), Dialog.Kind.MAGIC_WORDS);

                    int spellSound = spell.getWav();
                    soundEntitySystem.add(playerId, spellSound);

                    EntityUpdate victimUpdate = victimUpdateBuilder.build();
                    entityUpdateSystem.add(victimUpdate, UpdateTo.ALL);

                    EntityUpdate playerUpdate = playerUpdateBuilder.withComponents(magicWords).build();
                    entityUpdateSystem.add(playerUpdate, UpdateTo.ALL);
                } else if (spell.isImmobilize()) { /*Inmovilizar*/
                    targetEntity.immobile();
                    victimUpdateToAllBuilder.withComponents( targetEntity.getImmobile() );
                    updateMana( playerId, requiredMana, mana );
                } else if (spell.isRemoveParalysis()) {
                    if (targetEntity.isImmobile()) {
                        targetEntity.immobile(false);
                        victimUpdateToAllBuilder.remove(Immobile.class);
                        updateMana(playerId, requiredMana, mana);
                    } else {
                        notifyInfo(playerId, Messages.NOT_PARALYSIS);
                    }
                } else if (spell.isSumStrength()) { /*Sumar fuerza*/
                    int random = new Random().nextInt(spell.getMaxStrength() - spell.getMinStrength() + 1) + spell.getMinStrength();
                    targetEntity.strengthCurrentValue(targetEntity.strengthCurrentValue() + random);
                    targetEntity.buff().buffAddAttribute(targetEntity.getStrength(), spell.getStrengthDuration());
                    sendAttributeUpdate(target, targetEntity.getStrength(), targetEntity.getBuff());
                    updateMana(playerId, requiredMana, mana);
                } else if (spell.isSumAgility()) { /*Sumar agilidad*/
                    int random = new Random().nextInt(spell.getMaxAgility() - spell.getMinAgility() + 1) + spell.getMinAgility();
                    targetEntity.agilityCurrentValue(targetEntity.agilityCurrentValue() + random);
                    targetEntity.buff().buffAddAttribute(targetEntity.getAgility(), spell.getAgilityDuration());
                    sendAttributeUpdate(target, targetEntity.getAgility(), targetEntity.getBuff());
                    updateMana(playerId, requiredMana, mana);
                }
            } else {
                notifyInfo(playerId,Messages.NOT_ENOUGHT_MANA);
            }
        } else {
            notifyInfo(playerId, Messages.DEAD_CANT_ATTACK);
        }
    }

    private int calculateMagicDamage(int user, int target, Spell spell) {
        int damage;
        int magicDefense;
        final int minHP = spell.getMinHP();
        final int maxHP = spell.getMaxHP();
        damage = ThreadLocalRandom.current().nextInt(minHP, maxHP + 1);
        damage = E(user).levelLevel() + damage;
        characterTrainingSystem.userTakeDamage(user, target, damage);
        if (spell.getSumHP() == 1) { // HEAL

        } else if (spell.getSumHP() == 2) {
            if (E(target).hasHelmet()) {
                // TODO: ¿la defensa de casco es directamente Magic defense?
                magicDefense = getHeadDefense(target);
            }
            // TODO anillos
            damage = -damage;
        }
        return damage;
    }

    private int getHeadDefense(int entityId) {
        int min = 0, max = 1;
        E entity = E(entityId);
        if (entity.hasHelmet()) {
            int index = entity.getHelmet().index;
            HelmetObj obj = (HelmetObj) objectSystem.getObject(index).get();
            min = obj.getMinDef();
            max = obj.getMaxDef();
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
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

        boolean valid = false;

        // @todo usar un enum para darle significado a estos números
        switch (spellTarget) {
            case 1:
                valid = targetEntity.isCharacter() || (targetEntity.hasNPC() && targetEntity.isHostile());
                break;
            case 2:
                valid = (targetEntity.hasNPC() && targetEntity.isHostile());
                break;
            case 3:
                valid = targetEntity.isCharacter() || (targetEntity.hasNPC() && targetEntity.isHostile());
                break;
            case 4:
                valid = targetEntity == null;
                break;
        };

        return valid;
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