package game.managers;

import entity.*;
import entity.character.CanWrite;
import entity.character.Character;
import entity.character.info.Clan;
import entity.character.info.Description;
import entity.character.info.Name;
import entity.character.states.*;
import entity.character.status.*;
import interfaces.IUpdateProcessor;

import static com.artemis.E.E;

public class ComponentUpdateProcessor implements IUpdateProcessor {

    private final static ComponentUpdateProcessor instance;

    private static int entityId;

    static {
        instance = new ComponentUpdateProcessor();
    }

    public static ComponentUpdateProcessor getInstance() {
        return instance;
    }

    public static void setEntityId(int entityId) {
        ComponentUpdateProcessor.entityId = entityId;
    }

    @Override
    public void process(Clan clan) {
        E(entityId).clanName(clan.name);
    }

    @Override
    public void process(Description description) {
        E(entityId).descriptionText(description.text);
    }

    @Override
    public void process(Name name) {
        E(entityId).nameText(name.text);
    }

    @Override
    public void process(Elv elv) {
        E(entityId).elvElv(elv.elv);
    }

    @Override
    public void process(Exp exp) {
        E(entityId).expExp(exp.exp);
    }

    @Override
    public void process(Criminal criminal) {
        E(entityId).criminal();
    }

    @Override
    public void process(Immobile immobile) {
        E(entityId).immobile();
    }

    @Override
    public void process(Meditating meditating) {
        E(entityId).meditating();
    }

    @Override
    public void process(Navigating navigating) {
        E(entityId).navigating();
    }

    @Override
    public void process(Resting resting) {
        E(entityId).resting();
    }

    @Override
    public void process(Writing writing) {
        E(entityId).writing();
    }

    @Override
    public void process(GM gm) {
        E(entityId).gM();
    }

    @Override
    public void process(Health health) {
        E(entityId).healthMax(health.max);
        E(entityId).healthMin(health.min);
    }

    @Override
    public void process(Hungry hungry) {
        E(entityId).hungryMax(hungry.max);
        E(entityId).hungryMin(hungry.min);
    }

    @Override
    public void process(Mana mana) {
        E(entityId).manaMax(mana.max);
        E(entityId).manaMin(mana.min);
    }

    @Override
    public void process(Level level) {
        E(entityId).levelLevel(level.level);
    }

    @Override
    public void process(Stamina stamina) {
        E(entityId).staminaMax(stamina.max);
        E(entityId).staminaMin(stamina.min);
    }

    @Override
    public void process(Thirst thirst) {
        E(entityId).thirstMax(thirst.max);
        E(entityId).thirstMin(thirst.min);
    }

    @Override
    public void process(CanWrite canWrite) {
        E(entityId).canWrite();
    }

    @Override
    public void process(Character character) {
        E(entityId).character();
    }

    @Override
    public void process(Body body) {
        E(entityId).bodyIndex(body.index);
    }

    @Override
    public void process(Dialog dialog) {
        E(entityId).dialogTime(dialog.time);
        E(entityId).dialogAlpha(dialog.alpha);
        E(entityId).dialogText(dialog.text);
    }

    @Override
    public void process(Head head) {
        E(entityId).headIndex(head.index);
    }

    @Override
    public void process(Heading heading) {
        E(entityId).headingCurrent(heading.current);
    }

    @Override
    public void process(Helmet helmet) {
        E(entityId).helmetIndex(helmet.index);
    }

    @Override
    public void process(Shield shield) {
        E(entityId).shieldIndex(shield.index);
    }

    @Override
    public void process(Weapon weapon) {
        E(entityId).weaponIndex(weapon.index);
    }
}
