package server.systems.world.entity.training;

import com.artemis.ComponentMapper;
import com.badlogic.gdx.math.MathUtils;
import component.console.ConsoleMessage;
import component.entity.character.attributes.Constitution;
import component.entity.character.attributes.Intelligence;
import component.entity.character.info.CharHero;
import component.entity.character.info.Gold;
import component.entity.character.status.*;
import component.entity.npc.NPC;
import component.entity.world.CombatMessage;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.systems.config.NPCSystem;
import server.systems.network.EntityUpdateSystem;
import server.systems.network.MessageSystem;
import server.systems.world.WorldEntitiesSystem;
import server.systems.world.entity.factory.EffectEntitySystem;
import server.systems.world.entity.factory.SoundEntitySystem;
import server.systems.world.entity.user.ModifierSystem;
import server.utils.UpdateTo;
import shared.interfaces.CharClass;
import shared.interfaces.FXs;
import shared.network.notifications.EntityUpdate;
import shared.util.EntityUpdateBuilder;
import shared.util.Messages;
import shared.util.Pair;

import java.util.concurrent.ThreadLocalRandom;

import static server.database.model.modifiers.Modifiers.HEALTH;

public class CharacterTrainingSystem extends PassiveSystem {

    private static final int HIT_BREAKING_LEVEL = 35;
    public static int INITIAL_LEVEL = 1;
    public static int DEFAULT_STAMINA = 15;

    private EntityUpdateSystem entityUpdateSystem;
    private WorldEntitiesSystem worldEntitiesSystem;
    private NPCSystem npcSystem;
    private SoundEntitySystem soundEntitySystem;
    private EffectEntitySystem effectEntitySystem;
    private MessageSystem messageSystem;
    private ModifierSystem modifierSystem;

    ComponentMapper<Level> mLevel;
    ComponentMapper<Mana> mMana;
    ComponentMapper<Health> mHealth;
    ComponentMapper<Hit> mHit;
    ComponentMapper<Stamina> mStamina;
    ComponentMapper<NPC> mNPC;
    ComponentMapper<Gold> mGold;
    ComponentMapper<CharHero> mCharHero;
    ComponentMapper<Intelligence> mIntelligence;
    ComponentMapper<Constitution> mConstitution;

    public void userTakeDamage(int entityId, int target, int effectiveDamage) {
        int exp = getExp(target, effectiveDamage);

        if (mLevel.has(entityId) && exp > 0) {
            messageSystem.add(entityId, ConsoleMessage.combat(Messages.EXP_GAIN.name(), Integer.toString(exp)));
            Level level = mLevel.get(entityId);
            level.exp += exp;
            userCheckLevel(entityId);
        }
    }

    public void takeGold(int takerId, int giverId) {
        int goldAmount = getGold(giverId);
        if (mGold.has(takerId)) {
            Gold gold = mGold.get(takerId);
            gold.setCount(gold.getCount() + goldAmount);
            EntityUpdate update = EntityUpdateBuilder
                    .of(takerId)
                    .withComponents(gold, CombatMessage.energy("+" + goldAmount))
                    .build();
            entityUpdateSystem.add(update, UpdateTo.NEAR);
            messageSystem.add(takerId, ConsoleMessage.warning(Messages.GOLD_GAIN.name(), goldAmount + ""));
        }
    }


    private int getExp(int entityId, int effectiveDamage) {
        int exp = 0;
        if (mNPC.has(entityId)) {
            Health health = mHealth.get(entityId);
            int npcId = mNPC.get(entityId).getId();
            exp = npcSystem.getNpcs().get(npcId).getGiveEXP() * effectiveDamage / health.max;
        }

        return exp;
    }

    private int getGold(int entityId) {
        int gold = 0;
        if (mNPC.has(entityId)) { // @todo ¿por qué solo puede tomarse oro de los NPC?
            int npcId = mNPC.get(entityId).getId();
            gold = npcSystem.getNpcs().get(npcId).getGiveGLD();
        }

        return gold;
    }

    private void userCheckLevel(int entityId) {
        Level level = mLevel.get(entityId);
        if (level.exp > level.expToNextLevel) {
            int MAX_LEVEL = 45;
            if (level.level < MAX_LEVEL) {
                levelUp(entityId);
            } else {
                level.exp = 0;
                level.expToNextLevel = 0;
            }
        } else {
            entityUpdateSystem.add(EntityUpdateBuilder.of(entityId).withComponents(level).build(), UpdateTo.ENTITY);
        }
    }

    private void levelUp(int entityId) {
        soundEntitySystem.add(entityId, 3);
        // set new experience
        Level level = mLevel.get(entityId);
        level.exp -= level.expToNextLevel;
        level.level++;
        setNextRequiredExperience(level);
        // add attributes
        int mana = addMana(entityId);
        float health = addHealth(entityId);
        Pair<Integer, Integer> hit = addHit(entityId);
        int stamina = addStamina(entityId);
        // notify user
        notifyUpgrade(entityId, mana, health, hit, stamina);
        // Log.info("hp: "+ health + "mAna" + mana + "hit: " + hit);
    }

    private void notifyUpgrade(int entityId, int mana, float health, Pair<Integer, Integer> hit, int stamina) {
        // send message to user component.console
        messageSystem.add(entityId, ConsoleMessage.info(Messages.LEVEL_UP.name(), Float.toString(health), Integer.toString(mana), hit.getValue().toString(), Integer.toString(stamina)));

        // send user stat info
        EntityUpdate update = EntityUpdateBuilder.of(entityId)
                .withComponents(mLevel.get(entityId), mHealth.get(entityId), mMana.get(entityId), mHit.get(entityId), mStamina.get(entityId))
                .build();
        entityUpdateSystem.add(update, UpdateTo.ENTITY);
        effectEntitySystem.addFX(entityId, FXs.FX_LEVEL_UP, 1);
    }

    private void setNextRequiredExperience(Level level) {
        float modifier;
        if (level.level < 15) {
            modifier = 1.2f;
        } else if (level.level < 21) {
            modifier = 1.25f;
        } else if (level.level < 26) {
            modifier = 1.3f;
        } else if (level.level < 35) {
            modifier = 1.35f;
        } else if (level.level < 40) {
            modifier = 1.36f;
        } else {
            modifier = 1.4f;
        }
        level.expToNextLevel *= modifier;
    }

    private int addMana(int entityId) {
        CharClass charClass = CharClass.of(mCharHero.get(entityId).heroId);
        float manaPerLvlFactor;
        switch (charClass) {
            case ROGUE:
                manaPerLvlFactor = (float) (1 / 3) * 2;
                break;
            case PALADIN:
            case ASSASSIN:
                manaPerLvlFactor = 1;
                break;
            case MAGICIAN:
                manaPerLvlFactor = 2.8f;
                break;
            case BARDIC:
            case CLERIC:
            case DRUID:
                manaPerLvlFactor = 2f;
                break;
            default:
                manaPerLvlFactor = 0;
                break;
        }
        int manaUp = (int) (mIntelligence.get(entityId).getBaseValue() * manaPerLvlFactor);
        mMana.get(entityId).max += manaUp;
        return manaUp;
    }

    private float addHealth(int entityId) {
        int constitution = mConstitution.get(entityId).getBaseValue();
        float healthModifier = modifierSystem.of(HEALTH, CharClass.of(mCharHero.get(entityId).getHeroId()));
        float average = healthModifier - (21 - constitution) * 0.5f;
        int hpUp = ThreadLocalRandom.current().nextInt(getMinHealth(average), getMaxHealth(average));
        mHealth.get(entityId).max += hpUp;
        return hpUp;
    }

    private int getMaxHealth(float average) {
        return (int) (average % 1 == 0 ? average + 2 : average + 1.5f);
    }

    private int getMinHealth(float average) {
        return (int) (average % 1 == 0 ? average - 2 : average - 1.5f);
    }

    private Pair<Integer, Integer> addHit(int entityId) {
        CharClass charClass = CharClass.of(mCharHero.get(entityId).getHeroId());
        int minLvl;
        int maxLvl;
        int hit;
        switch (charClass) {
            case WARRIOR:
            case ARCHER:
                minLvl = 3;
                maxLvl = 2;
                break;
            case PIRATE:
                minLvl = 3;
                maxLvl = 3;
                break;
            case PALADIN:
            case ASSASSIN:
            case ROGUE:
                minLvl = 3;
                maxLvl = 1;
                break;
            case CLERIC:
            case BARDIC:
            case DRUID:
            case THIEF:
                minLvl = 2;
                maxLvl = 2;
                break;
            case MAGICIAN:
                minLvl = 1;
                maxLvl = 1;
                break;
            default:
                minLvl = 0;
                maxLvl = 0;
                break;
        }
        hit = mLevel.get(entityId).level < HIT_BREAKING_LEVEL ? minLvl : maxLvl;
        int STAT_MAXHIT_UNDER36 = 99;
        int STAT_MAXHIT_OVER36 = 999;
        int minHit = MathUtils.clamp(mHit.get(entityId).getMin() + hit, 0, mLevel.get(entityId).level < 35 ? STAT_MAXHIT_UNDER36 : STAT_MAXHIT_OVER36);
        int maxHit = MathUtils.clamp(mHit.get(entityId).getMax() + hit, 0, mLevel.get(entityId).level < 35 ? STAT_MAXHIT_UNDER36 : STAT_MAXHIT_OVER36);

        mHit.get(entityId).setMin(minHit);
        mHit.get(entityId).setMax(maxHit);
        return new Pair<>(minHit, maxHit);
    }

    private int addStamina(int entityId) {
        CharClass charClass = CharClass.of(mCharHero.get(entityId).getHeroId());
        int stamina = DEFAULT_STAMINA;
        switch (charClass) {
            case ROGUE:
            case THIEF:
                stamina += 3;
                break;
            case MAGICIAN:
                stamina -= 1;
                break;
        }
        mStamina.get(entityId).min += stamina;
        mStamina.get(entityId).max += stamina;
        return stamina;
    }
}
