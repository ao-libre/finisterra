package shared.model;

import camera.Focused;
import com.artemis.Component;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player implements IEntity {

    WorldPos worldPos;
    Heading heading;
    Name name;
    Elv elv;
    Exp exp;
    Optional<GM> gm;
    Health health;
    Hungry hungry;
    Level level;
    Mana mana;
    Stamina stamina;
    Thirst thirst;
    Body body;
    Head head;
    Network network;
    Optional<Focused> focused;
    Optional<Clan> clan;
    Optional<Immobile> immobile;
    Optional<Meditating> meditating;
    Optional<Navigating> navigating;
    Optional<Resting> resting;
    Optional<Criminal> criminal;
    Optional<CanWrite> canWrite;
    Optional<Dialog> dialog;
    Optional<Helmet> helmet;
    Optional<Shield> shield;
    Optional<Weapon> weapon;
    Optional<Description> description;

    public Player() {
    }

    public Optional<Description> getDescription() {
        return description;
    }

    public void setDescription(Optional<Description> description) {
        this.description = description;
    }

    public WorldPos getWorldPos() {
        return worldPos;
    }

    public void setWorldPos(WorldPos worldPos) {
        this.worldPos = worldPos;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public Optional<Focused> getFocused() {
        return focused;
    }

    public void setFocused(Optional<Focused> focused) {
        this.focused = focused;
    }

    public Optional<Clan> getClan() {
        return clan;
    }

    public void setClan(Optional<Clan> clan) {
        this.clan = clan;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Optional<Immobile> getImmobile() {
        return immobile;
    }

    public void setImmobile(Optional<Immobile> immobile) {
        this.immobile = immobile;
    }

    public Optional<Meditating> getMeditating() {
        return meditating;
    }

    public void setMeditating(Optional<Meditating> meditating) {
        this.meditating = meditating;
    }

    public Optional<Navigating> getNavigating() {
        return navigating;
    }

    public void setNavigating(Optional<Navigating> navigating) {
        this.navigating = navigating;
    }

    public Optional<Resting> getResting() {
        return resting;
    }

    public void setResting(Optional<Resting> resting) {
        this.resting = resting;
    }

    public Optional<Criminal> getCriminal() {
        return criminal;
    }

    public void setCriminal(Optional<Criminal> criminal) {
        this.criminal = criminal;
    }

    public Elv getElv() {
        return elv;
    }

    public void setElv(Elv elv) {
        this.elv = elv;
    }

    public Exp getExp() {
        return exp;
    }

    public void setExp(Exp exp) {
        this.exp = exp;
    }

    public Optional<GM> getGm() {
        return gm;
    }

    public void setGm(Optional<GM> gm) {
        this.gm = gm;
    }

    public Health getHealth() {
        return health;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public Hungry getHungry() {
        return hungry;
    }

    public void setHungry(Hungry hungry) {
        this.hungry = hungry;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Mana getMana() {
        return mana;
    }

    public void setMana(Mana mana) {
        this.mana = mana;
    }

    public Stamina getStamina() {
        return stamina;
    }

    public void setStamina(Stamina stamina) {
        this.stamina = stamina;
    }

    public Thirst getThirst() {
        return thirst;
    }

    public void setThirst(Thirst thirst) {
        this.thirst = thirst;
    }

    public Optional<CanWrite> getCanWrite() {
        return canWrite;
    }

    public void setCanWrite(Optional<CanWrite> canWrite) {
        this.canWrite = canWrite;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Optional<Dialog> getDialog() {
        return dialog;
    }

    public void setDialog(Optional<Dialog> dialog) {
        this.dialog = dialog;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Optional<Helmet> getHelmet() {
        return helmet;
    }

    public void setHelmet(Optional<Helmet> helmet) {
        this.helmet = helmet;
    }

    public Optional<Shield> getShield() {
        return shield;
    }

    public void setShield(Optional<Shield> shield) {
        this.shield = shield;
    }

    public Optional<Weapon> getWeapon() {
        return weapon;
    }

    public void setWeapon(Optional<Weapon> weapon) {
        this.weapon = weapon;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public List<Component> getComponents() {
        List<Component> components = new ArrayList<>();
        components.add(worldPos);
        components.add(heading);
        components.add(name);
        components.add(elv);
        components.add(exp);
        components.add(health);
        components.add(hungry);
        components.add(level);
        components.add(mana);
        components.add(stamina);
        components.add(thirst);
        components.add(body);
        components.add(head);
        components.add(network);
        focused.ifPresent(it -> components.add(it));
        clan.ifPresent(it -> components.add(it));
        immobile.ifPresent(it -> components.add(it));
        meditating.ifPresent(it -> components.add(it));
        navigating.ifPresent(it -> components.add(it));
        resting.ifPresent(it -> components.add(it));
        criminal.ifPresent(it -> components.add(it));
        gm.ifPresent(it -> components.add(it));
        canWrite.ifPresent(it -> components.add(it));
        dialog.ifPresent(it -> components.add(it));
        helmet.ifPresent(it -> components.add(it));
        shield.ifPresent(it -> components.add(it));
        weapon.ifPresent(it -> components.add(it));
        description.ifPresent(it -> components.add(it));
        return components;
    }
}
