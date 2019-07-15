package server.systems;

import com.artemis.E;
import com.artemis.annotations.Wire;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.minlog.Log;
import entity.character.status.Health;
import entity.character.status.Level;
import entity.world.CombatMessage;
import game.systems.assets.AssetsSystem;
import graphics.Effect;
import net.mostlyoriginal.api.system.core.PassiveSystem;
import server.database.model.modifiers.Modifiers;
import server.systems.manager.NPCManager;
import server.systems.manager.WorldManager;
import shared.interfaces.CharClass;
import shared.interfaces.FXs;
import shared.model.npcs.NPC;
import shared.network.notifications.ConsoleMessage;
import shared.network.notifications.EntityUpdate;
import shared.network.notifications.EntityUpdate.EntityUpdateBuilder;
import shared.util.Messages;
import shared.util.Pair;

import java.util.concurrent.ThreadLocalRandom;

import static com.artemis.E.E;
import static java.lang.String.format;

@Wire
public class CharacterTrainingSystem extends PassiveSystem {

    public static final int HIT_BREAKING_LEVEL = 35;
    public static int INITIAL_LEVEL = 1;
    public static int DEFAULT_STAMINA = 15;
    public static int STAT_MAXHIT_UNDER36 = 99;
    public static int STAT_MAXHIT_OVER36 = 999;
    private static int MAX_LEVEL = 45;
    private WorldManager worldManager;
    private NPCManager npcManager;
    private AssetsSystem assetsSystem;

    public void userTakeDamage(int entityId, int target, int effectiveDamage) {
        int exp = getExp(target, effectiveDamage);

        E e = E(entityId);
        if (e.hasLevel() && exp > 0) {
            worldManager.sendEntityUpdate(entityId, ConsoleMessage.combat(assetsSystem.getAssetManager().getMessages(Messages.EXP_GAIN.name(), exp)));
            Level level = e.getLevel();
            level.exp += exp;
            userCheckLevel(entityId);
        }
    }

    public void takeGold(int userId, int entityId) {
        int gold = getGold(entityId);
        E e = E(userId);
        if (e.hasGold()) {
            e.getGold().setCount(e.getGold().getCount() + gold);
            worldManager.sendEntityUpdate(userId, EntityUpdateBuilder.of(userId).withComponents(e.getGold(), CombatMessage.energy("+" + gold)));
        }
    }


    private int getExp(int target, int effectiveDamage) {
        int exp = 0;
        E npc = E(target);
        if (npc.hasNPC()) {
            Health health = npc.getHealth();
            int id = npc.getNPC().id;
            NPC npcInfo = npcManager.getNpcs().get(id);
            exp = npcInfo.getGiveEXP() * effectiveDamage / health.max;
        }

        return exp;
    }

    private int getGold(int entityId) {
        int gold = 0;
        E npc = E(entityId);
        if (npc.hasNPC()) {
            int id = npc.getNPC().id;
            NPC npcInfo = npcManager.getNpcs().get(id);
            gold = npcInfo.getGiveGLD();
        }

        return gold;
    }

    private void userCheckLevel(int userId) {
        assert (E(userId).hasLevel());
        Level level = E(userId).getLevel();
        if (level.exp > level.expToNextLevel) {
            if (level.level < MAX_LEVEL) {
                levelUp(userId);
            } else {
                level.exp = 0;
                level.expToNextLevel = 0;
            }
        } else {
            worldManager.sendEntityUpdate(userId, EntityUpdateBuilder.of(userId).withComponents(E(userId).getLevel()).build());
        }

    }

    private void levelUp(int userId) {
        // set new experience
        Level level = E(userId).getLevel();
        level.exp -= level.expToNextLevel;
        level.level++;
        setNextRequiredExperience(level);
        // add attributes
        int mana = addMana(userId);
        float health = addHealth(userId);
        Pair<Integer, Integer> hit = addHit(userId);
        int stamina = addStamina(userId);
        // notify user
        notifyUpgrade(userId, mana, health, hit, stamina);
        // Log.info("hp: "+ health + "mAna" + mana + "hit: " + hit);
    }

    private void notifyUpgrade(int userId, int mana, float health, Pair<Integer, Integer> hit, int stamina) {
        E e = E(userId);
        EntityUpdate update = EntityUpdateBuilder.of(userId)
                .withComponents(e.getLevel(), e.getHealth(), e.getMana(), e.getHit(), e.getStamina())
                .build();
        worldManager.sendEntityUpdate(userId, update);
        worldManager.sendEntityUpdate(userId, ConsoleMessage.info(getMessage(mana, health, hit, stamina)));
        int fxE = world.create();
        Effect effect = new Effect.EffectBuilder().attachTo(userId).withLoops(1).withFX(FXs.FX_LEVEL_UP).build();
        EntityUpdate fxUpdate = EntityUpdateBuilder.of(fxE).withComponents(effect).build();
        worldManager.notifyUpdate(userId, fxUpdate);
        worldManager.unregisterEntity(fxE);
    }

    private String getMessage(int mana, float health, Pair<Integer, Integer> hit, int stamina) {
        // TODO refector MessageLvl
        return ("Level up!" + " Recibiste " + health + " puntos de vida y " + mana + " Puntos de mana. Tu golpe máximo aumento. " + hit);
    }

    private void setNextRequiredExperience(Level level) {
        float modifier = 1;
        if (level.level < 15) {
            modifier = 1.4f;
        } else if (level.level < 21) {
            modifier = 1.35f;
        } else if (level.level < 26) {
            modifier = 1.3f;
        } else if (level.level < 35) {
            modifier = 1.2f;
        } else if (level.level < 40) {
            modifier = 1.3f;
        } else {
            modifier = 1.475f;
        }
        level.expToNextLevel *= modifier;
    }

    private int addMana(int userId) {
        E e = E(userId);
        CharClass heroClass = CharClass.of(e);
        float manaPerLvlFactor;
        switch (heroClass) {
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
        int manaUp = (int) (e.intelligenceBaseValue() * manaPerLvlFactor);
        e.getMana().max += manaUp;
        return manaUp;
    }

    private float addHealth(int userId) {
        E e = E(userId);
        int constitution = e.constitutionBaseValue();
        float healthModifier = Modifiers.HEALTH.of(CharClass.of(e));
        float average = healthModifier - (21 - constitution) * 0.5f;
        int hpUp = ThreadLocalRandom.current().nextInt(getMinHealth(average), getMaxHealth(average));
        e.getHealth().max += hpUp;
        return hpUp;
    }

    private int getMaxHealth(float average) {
        return (int) (average % 1 == 0 ? average + 2 : average + 1.5f);
    }

    private int getMinHealth(float average) {
        return (int) (average % 1 == 0 ? average - 2 : average - 1.5f);
    }

    private Pair<Integer, Integer> addHit(int userId) {
        E entity = E(userId);
        CharClass charClass = CharClass.of(entity);
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
        hit = entity.getLevel().level < HIT_BREAKING_LEVEL ? minLvl : maxLvl;
        int minHit = MathUtils.clamp(entity.hitMin() + hit, 0, entity.getLevel().level < 35 ? STAT_MAXHIT_UNDER36 : STAT_MAXHIT_OVER36);
        int maxHit = MathUtils.clamp(entity.hitMax() + hit, 0, entity.getLevel().level < 35 ? STAT_MAXHIT_UNDER36 : STAT_MAXHIT_OVER36);

        entity.hitMax(maxHit).hitMin(minHit);
        return new Pair(minHit, maxHit);
    }

    private int addStamina(int userId) {
        E entity = E(userId);
        CharClass charClass = CharClass.of(entity);
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
        entity.getStamina().min += stamina;
        entity.getStamina().max += stamina;
        return stamina;
    }

}
