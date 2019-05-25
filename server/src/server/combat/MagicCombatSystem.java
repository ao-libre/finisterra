package server.combat;

import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.E;
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
import server.core.Server;
import server.systems.manager.WorldManager;
import shared.model.Spell;
import shared.network.combat.SpellCastRequest;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.objects.types.HelmetObj;
import shared.objects.types.Obj;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static java.lang.String.format;
import static shared.util.Messages.*;

public class MagicCombatSystem extends BaseSystem {

    public static final String SPACE = " ";
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
            getServer().getWorldManager()
                    .notifyUpdate(userId, EntityUpdateBuilder.of(userId).withComponents(new AttackAnimation()).build());
            castSpell(userId, target.get(), spell);
        }
    }

    private Optional<Integer> getTarget(int userId, WorldPos worldPos, long timestamp) {
        Set<Integer> entities = new HashSet<>(getServer().getMapManager().getNearEntities(userId));
        entities.add(userId);
        // TODO check timestamp?
        return entities
                .stream()
                .map(E::E)
                .filter(Objects::nonNull)
                .filter(E::hasWorldPos)
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
        int requiredStamina = spell.getRequiredStamina(); // TODO check stamina
        Mana mana = E(playerId).getMana();
        Stamina stamina = E(playerId).getStamina();

        EntityUpdateBuilder playerUpdateBuilder = EntityUpdateBuilder.of(playerId);
        EntityUpdateBuilder victimUpdateBuilder = EntityUpdateBuilder.of(target);
        EntityUpdateBuilder victimUpdateToAllBuilder = EntityUpdateBuilder.of(target);



        if (mana.min > requiredMana) {
            if (!isValid(target, spell)) {
                notifyInfo(playerId, INVALID_TARGET);
                return;
            }

            if (stamina.min < requiredStamina) {
                notifyInfo(playerId, NOT_ENOUGH_ENERGY);
                return;
            }

            if (playerId == target) {
                if (spell.getSumHP() == 2 || spell.isImmobilize() || spell.isParalyze()) {
                    notifyMagic(playerId, CANT_ATTACK_YOURSELF);
                    return;
                }
                notifyMagic(playerId, spell.getOwnerMsg());
            } else {
                notifyMagic(playerId, spell.getOriginMsg() + SPACE + getName(target));
                notifyMagic(target, getName(playerId) + SPACE + spell.getTargetMsg());
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
                    notifyMagic(playerId, format(HEAL_TO, getName(target), Math.abs(damage)));
                    notifyMagic(target, format(HEAL_BY, getName(playerId), Math.abs(damage)));
                } else {
                    notifyMagic(playerId, format(DAMAGE_TO, Math.abs(damage), getName(target)));
                    notifyMagic(target, format(DAMAGED_BY, getName(playerId), Math.abs(damage)));
                }
                if (health.min <= 0) {
                    getWorldManager().userDie(target);
                    notifyMagic(playerId, format(KILL, getName(target)));
                    notifyMagic(target, format(KILLED, getName(playerId)));
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
                    notifyInfo(playerId, NOT_PARALYSIS);
                    return;
                }
            }

            if (spell.isSumStrength()){
                int random = new Random().nextInt(spell.getMaxStrength() - spell.getMinStrength() + 1) + spell.getMinStrength();
                targetEntity.strengthCurrentValue(targetEntity.strengthCurrentValue() + random);
                targetEntity.buff().buffAddAttribute(targetEntity.getStrength(),spell.getStrengthDuration());
                SendAttributeUpdate(target,targetEntity.getStrength(),targetEntity.getBuff());
            }

            if (spell.isSumAgility()){
                int random = new Random().nextInt(spell.getMaxAgility() - spell.getMinAgility() + 1) + spell.getMinAgility();
                targetEntity.agilityCurrentValue(targetEntity.agilityCurrentValue() + random);
                targetEntity.buff().buffAddAttribute(targetEntity.getAgility(),spell.getAgilityDuration());
                SendAttributeUpdate(target,targetEntity.getAgility(),targetEntity.getBuff());
            }

            if (fxGrh > 0) {
                int fxE = world.create();
                Effect effect = new EffectBuilder().attachTo(target).withLoops(Math.max(1, spell.getLoops())).withFX(fxGrh - 1).build();
                EntityUpdate fxUpdate = EntityUpdateBuilder.of(fxE).withComponents(effect).build();
                getWorldManager().notifyUpdate(target, fxUpdate);
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
        EntityUpdate update = EntityUpdateBuilder.of(playerId).withComponents(mana).build();
        getWorldManager().sendEntityUpdate(playerId, update);
    }

    protected void SendAttributeUpdate(int player, Attribute attribute, Buff buff) {
        EntityUpdate updateAGI = EntityUpdateBuilder.of(E(player).id()).withComponents(attribute, buff).build();
        getServer().getWorldManager().sendEntityUpdate(player, updateAGI);
    }

    private boolean isValid(int target, Spell spell) {
        E targetEntity = E(target);
        int spellTarget = spell.getTarget();
        switch (spellTarget) {
            case 1:
                return targetEntity.isCharacter() || targetEntity.isNPC();
            case 2:
                return targetEntity.isNPC();
            case 3:
                return targetEntity.isCharacter() || targetEntity.isNPC();
            case 4:
                return targetEntity == null;
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
        return world.getSystem(WorldManager.class);
    }

    @Override
    protected void processSystem() {
    }
}
