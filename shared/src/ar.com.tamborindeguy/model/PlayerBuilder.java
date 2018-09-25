package ar.com.tamborindeguy.model;

import camera.Focused;
import com.artemis.Entity;
import entity.*;
import entity.character.CanWrite;
import entity.character.info.Clan;
import entity.character.info.Description;
import entity.character.info.Name;
import entity.character.states.Immobile;
import entity.character.states.Meditating;
import entity.character.states.Navigating;
import entity.character.states.Resting;
import entity.character.status.*;
import network.Network;
import position.WorldPos;

import java.util.Optional;

public class PlayerBuilder {

    private Player player;

    public PlayerBuilder() {
        player = new Player();
    }

    public PlayerBuilder with(Name name) {
        player.setName(name);
        return this;
    }

    public PlayerBuilder with(Description description) {
        player.setDescription(Optional.ofNullable(description));
        return this;
    }

    public PlayerBuilder with(WorldPos worldPos) {
        player.setWorldPos(worldPos);
        return this;
    }

    public PlayerBuilder with(Heading heading) {
        player.setHeading(heading);
        return this;
    }

    public PlayerBuilder with(Focused focused) {
        player.setFocused(Optional.ofNullable(focused));
        return this;
    }

    public PlayerBuilder with(Clan clan) {
        player.setClan(Optional.ofNullable(clan));
        return this;
    }

    public PlayerBuilder with(Immobile immobile) {
        player.setImmobile(Optional.ofNullable(immobile));
        return this;
    }

    public PlayerBuilder with(Meditating meditating) {
        player.setMeditating(Optional.ofNullable(meditating));
        return this;
    }

    public PlayerBuilder with(Navigating navigating) {
        player.setNavigating(Optional.ofNullable(navigating));
        return this;
    }

    public PlayerBuilder with(Resting resting) {
        player.setResting(Optional.ofNullable(resting));
        return this;
    }

    public PlayerBuilder with(Criminal criminal) {
        player.setCriminal(Optional.ofNullable(criminal));
        return this;
    }

    public PlayerBuilder with(Elv elv) {
        player.setElv(elv);
        return this;
    }

    public PlayerBuilder with(Exp exp) {
        player.setExp(exp);
        return this;
    }

    public PlayerBuilder with(GM gm) {
        player.setGm(Optional.ofNullable(gm));
        return this;
    }

    public PlayerBuilder with(Health health) {
        player.setHealth(health);
        return this;
    }

    public PlayerBuilder with(Hungry hungry) {
        player.setHungry(hungry);
        return this;
    }

    public PlayerBuilder with(Level level) {
        player.setLevel(level);
        return this;
    }

    public PlayerBuilder with(Mana mana) {
        player.setMana(mana);
        return this;
    }

    public PlayerBuilder with(Thirst thirst) {
        player.setThirst(thirst);
        return this;
    }

    public PlayerBuilder with(CanWrite canWrite) {
        player.setCanWrite(Optional.ofNullable(canWrite));
        return this;
    }

    public PlayerBuilder with(Body body) {
        player.setBody(body);
        return this;
    }

    public PlayerBuilder with(Head head) {
        player.setHead(head);
        return this;
    }

    public PlayerBuilder with(Dialog dialog) {
        player.setDialog(Optional.ofNullable(dialog));
        return this;
    }

    public PlayerBuilder with(Helmet helmet) {
        player.setHelmet(Optional.ofNullable(helmet));
        return this;
    }

    public PlayerBuilder with(Shield shield) {
        player.setShield(Optional.ofNullable(shield));
        return this;
    }

    public PlayerBuilder with(Weapon weapon) {
        player.setWeapon(Optional.ofNullable(weapon));
        return this;
    }

    public PlayerBuilder with(Network network) {
        player.setNetwork(network);
        return this;
    }

    public PlayerBuilder with(Stamina stamina) {
        player.setStamina(stamina);
        return this;
    }

    public Player createPlayer(Entity entity) {
        return with(entity.getComponent(Focused.class))
                .with(entity.getComponent(WorldPos.class))
                .with(entity.getComponent(Clan.class))
                .with(entity.getComponent(Description.class))
                .with(entity.getComponent(Name.class))
                .with(entity.getComponent(Immobile.class))
                .with(entity.getComponent(Meditating.class))
                .with(entity.getComponent(Navigating.class))
                .with(entity.getComponent(Resting.class))
                .with(entity.getComponent(Criminal.class))
                .with(entity.getComponent(Elv.class))
                .with(entity.getComponent(Exp.class))
                .with(entity.getComponent(GM.class))
                .with(entity.getComponent(Health.class))
                .with(entity.getComponent(Hungry.class))
                .with(entity.getComponent(Level.class))
                .with(entity.getComponent(Mana.class))
                .with(entity.getComponent(Thirst.class))
                .with(entity.getComponent(Stamina.class))
                .with(entity.getComponent(CanWrite.class))
                .with(entity.getComponent(Body.class))
                .with(entity.getComponent(Dialog.class))
                .with(entity.getComponent(Head.class))
                .with(entity.getComponent(Heading.class))
                .with(entity.getComponent(Helmet.class))
                .with(entity.getComponent(Shield.class))
                .with(entity.getComponent(Weapon.class))
                .with(entity.getComponent(Network.class))
                .createPlayer();
    }

    public Player createPlayer() {
        return player;
    }


}