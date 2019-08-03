package server.systems.combat;

import com.artemis.BaseSystem;
import com.artemis.E;
import com.artemis.annotations.Wire;
import com.esotericsoftware.minlog.Log;
import entity.character.attributes.Attribute;
import entity.character.states.Buff;
import entity.character.states.Immobile;
import entity.character.status.Health;
import entity.character.status.Mana;
import entity.character.status.Stamina;
import entity.world.CombatMessage;
import entity.world.Dialog;
import graphics.Effect;
import graphics.Effect.EffectBuilder;
import physics.AttackAnimation;
import position.WorldPos;
import server.systems.manager.MapManager;
import server.systems.manager.ObjectManager;
import server.systems.manager.WorldManager;
import shared.model.Spell;
import shared.network.combat.SpellCastRequest;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.objects.types.HelmetObj;
import shared.objects.types.Obj;
import shared.util.Messages;
import shared.util.Messages.*;
import server.systems.CharacterTrainingSystem;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static java.lang.String.format;

@Wire
public class MagicCombatSystem extends BaseSystem {

    private static final String SPACE = " ";
    private static final int TIME_TO_MOVE_1_TILE = 200;

    // Injected Systems
    private MapManager mapManager;
    private WorldManager worldManager;
    private ObjectManager objectManager;
    private CharacterTrainingSystem characterTrainingSystem;

    public void spell(int userId, SpellCastRequest spellCastRequest) {
        final Spell spell = spellCastRequest.getSpell();
        final WorldPos targetPos = spellCastRequest.getWorldPos();
        final long timestamp = spellCastRequest.getTimestamp();
        Optional<Integer> target = getTarget(userId, targetPos, timestamp);
        if (target.isPresent()) {
            worldManager.notifyUpdate(userId, EntityUpdateBuilder.of(userId).withComponents(new AttackAnimation()).build());
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
        int requiredStamina = spell.getRequiredStamina(); // TODO check stamina
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
            int damage = 0;
            if (spell.getSumHP() > 0) {
                Health health = targetEntity.getHealth();
                damage = calculateMagicDamage(playerId, target, spell);
                health.min += damage;
                health.min = Math.max(0, health.min);
                victimUpdateToAllBuilder.withComponents(CombatMessage.magic(damage > 0 ? "+" : "-" + Math.abs(damage)));
                victimUpdateBuilder.withComponents(health);
                if (damage > 0) {
                    notifyMagic(playerId, Messages.HEAL_TO, getName(target), Math.abs(damage));
                    notifyMagic(target, Messages.HEAL_BY, getName(playerId), Math.abs(damage));
                } else {
                    notifyMagic(playerId, Messages.DAMAGE_TO, Math.abs(damage), getName(target));
                    notifyMagic(target, Messages.DAMAGED_BY, getName(playerId), Math.abs(damage));
                }
                if (health.min <= 0) {
                    getWorldManager().entityDie(target);
                    notifyMagic(playerId, Messages.KILL, getName(target));
                    notifyMagic(target, Messages.KILLED, getName(playerId));
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
                int fxE = world.create();
                EntityUpdateBuilder fxUpdate = EntityUpdateBuilder.of(fxE);
                Effect effect = new EffectBuilder().attachTo(target).withLoops(Math.max(1, spell.getLoops())).withFX(fxGrh).build();
                fxUpdate.withComponents(effect).build();
                if (targetEntity.hasWorldPos()) {
                    WorldPos worldPos = targetEntity.getWorldPos();
                    fxUpdate.withComponents(worldPos);
                }
                getWorldManager().notifyUpdate(target, fxUpdate.build());
                getWorldManager().unregisterEntity(fxE);
            }

            stamina.min -= requiredStamina;
            playerUpdateBuilder.withComponents(stamina);

            updateMana(playerId, requiredMana, mana);
            Dialog magicWords = new Dialog(spell.getMagicWords(), Dialog.Kind.MAGIC_WORDS);

            Log.info("Magic attack " + spell.getMagicWords());
            getWorldManager().sendEntityUpdate(target, victimUpdateBuilder.build());
            getWorldManager().notifyUpdate(target, victimUpdateToAllBuilder.build());
            getWorldManager().notifyUpdate(playerId, playerUpdateBuilder
                    .withComponents(magicWords).build());
        } else {
            notifyInfo(playerId, Messages.NOT_ENOUGHT_MANA);
        }
    }

    private int calculateMagicDamage(int user, int target, Spell spell) {
        int damage = 0;
        final int minHP = spell.getMinHP();
        final int maxHP = spell.getMaxHP();
        damage = ThreadLocalRandom.current().nextInt(minHP, maxHP + 1);
        damage = E(user).levelLevel() + damage;
        characterTrainingSystem.userTakeDamage(user, target, damage);
        if (spell.getSumHP() == 1) { // HEAL
            // TODO
        } else if (spell.getSumHP() == 2) {
            int magicDefense;
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
        return (int) (damage);
    }

    private void updateMana(int playerId, int requiredMana, Mana mana) {
        mana.min -= requiredMana;
        // update mana
        EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(mana).build();
        getWorldManager().sendEntityUpdate(playerId, update);
    }

    private void sendAttributeUpdate(int player, Attribute attribute, Buff buff) {
        EntityUpdate updateAGI = EntityUpdateBuilder.of(E(player).id()).withComponents(attribute, buff).build();
        worldManager.sendEntityUpdate(player, updateAGI);
    }

    private boolean isValid(int target, Spell spell) {
        E targetEntity = E(target);
        int spellTarget = spell.getTarget();
        switch (spellTarget) {
            case 1:
                return targetEntity.isCharacter() || (targetEntity.hasNPC() && targetEntity.isHostile());
            case 2:
                return (targetEntity.hasNPC() && targetEntity.isHostile());
            case 3:
                return targetEntity.isCharacter() || (targetEntity.hasNPC() && targetEntity.isHostile());
            case 4:
                return targetEntity == null;
        }

        return false;
    }

    private void notifyInfo(int userId, Messages messageId, Object... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.info(messageId, messageParams);
        getWorldManager().sendEntityUpdate(userId, combat);
    }

    private void notifyMagic(int userId, Messages messageId, Object... messageParams) {
        final ConsoleMessage combat = ConsoleMessage.combat(messageId, messageParams);
        getWorldManager().sendEntityUpdate(userId, combat);
    }

    private String getName(int userId) {
        return E(userId).getName().text;
    }

    private WorldManager getWorldManager() {
        return world.getSystem(WorldManager.class);
    }

    @Override
    protected void processSystem() {

    }
}
