package shared.model.npcs;

import shared.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class NPC {

    private int id;

    private String name;
    private int npcType;
    private String desc;
    private int head;
    private int heading;
    private int body;
    private int city;

    private boolean validWater;
    private boolean invalidEarth;
    private boolean faction;
    private boolean dobleAttack;

    //
    private int itemTypes;
    private boolean commerce;
    private List<Pair<Integer, Integer>> objs = new ArrayList<>();
    ;
    private boolean invReSpawn;

    //
    private int movement;
    private boolean attackable;
    private boolean respawn;
    private boolean hostile;
    private boolean domable;
    private boolean alignment;
    private boolean backup;
    private boolean poison;
    private boolean affectParalysis;
    //
    private int giveEXP;
    private int giveGLD;
    //
    private int attackPower;
    private int evasionPower;

    private int maxHP;
    private int minHP;
    private int maxHit;
    private int minHit;
    private int def;
    private int defM;


    private List<Pair<Integer, Integer>> drops = new ArrayList<>();
    private List<Integer> spells = new ArrayList<>();
    private List<Pair<Integer, String>> npcSpanwer = new ArrayList<>();
    private int attackSnd;
    private int walkSnd;
    private int dieSound;
    private int gHitSnd;
    private List<String> expressions = new ArrayList<>();

    public NPC(int id) {
        this.id = id;
    }

    public NPC(NPC other) {
        // TODO CHECK
        this.id = other.id;
        this.name = other.name;
        this.npcType = other.npcType;
        this.desc = other.desc;
        this.head = other.head;
        this.heading = other.heading;
        this.body = other.body;
        this.city = other.city;
        this.validWater = other.validWater;
        this.invalidEarth = other.invalidEarth;
        this.faction = other.faction;
        this.dobleAttack = other.dobleAttack;
        this.itemTypes = other.itemTypes;
        this.commerce = other.commerce;
        this.objs = other.objs;
        this.invReSpawn = other.invReSpawn;
        this.movement = other.movement;
        this.attackable = other.attackable;
        this.respawn = other.respawn;
        this.hostile = other.hostile;
        this.domable = other.domable;
        this.alignment = other.alignment;
        this.backup = other.backup;
        this.poison = other.poison;
        this.affectParalysis = other.affectParalysis;
        this.giveEXP = other.giveEXP;
        this.giveGLD = other.giveGLD;
        this.attackPower = other.attackPower;
        this.evasionPower = other.evasionPower;
        this.maxHP = other.maxHP;
        this.minHP = other.minHP;
        this.maxHit = other.maxHit;
        this.minHit = other.minHit;
        this.def = other.def;
        this.defM = other.defM;
        this.drops = other.drops;
        this.spells = other.spells;
        this.npcSpanwer = other.npcSpanwer;
        this.attackSnd = other.attackSnd;
        this.walkSnd = other.walkSnd;
        this.dieSound = other.dieSound;
        this.gHitSnd = other.gHitSnd;
        this.expressions = other.expressions;
    }

    public void addObj(int id, int count) {
        objs.add(new Pair<>(id, count));
    }

    public void addDrops(int id, int count) {
        drops.add(new Pair<>(id, count));
    }

    public void addSpells(int id) {
        spells.add(id);
    }

    public void addNPCtoSpawn(int id, String name) {
        npcSpanwer.add(new Pair<>(id, name));
    }

    public void addExpression(String expression) {
        expressions.add(expression);
    }

    public List<Pair<Integer, Integer>> getObjs() {
        return objs;
    }

    public List<Pair<Integer, Integer>> getDrops() {
        return drops;
    }

    public List<Integer> getSpells() {
        return spells;
    }

    public List<Pair<Integer, String>> getNpcSpanwer() {
        return npcSpanwer;
    }

    public int getAttackSnd() {
        return attackSnd;
    }

    public void setAttackSnd(int id) {
        this.attackSnd = id;
    }

    public int getWalkSnd() {
        return walkSnd;
    }

    public void setWalkSnd(int id) {
        this.walkSnd = id;
    }

    public int getDieSound() {
        return dieSound;
    }

    public void setDieSound(int id) {
        this.dieSound = id;
    }

    public int getGHitSnd() {
        return gHitSnd;
    }

    public void setGHitSnd(int id) {
        this.gHitSnd = id;
    }

    public List<String> getExpressions() {
        return expressions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNpcType() {
        return npcType;
    }

    public void setNpcType(int npcType) {
        this.npcType = npcType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getBody() {
        return body;
    }

    public void setBody(int body) {
        this.body = body;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(int itemTypes) {
        this.itemTypes = itemTypes;
    }

    public boolean isCommerce() {
        return commerce;
    }

    public void setCommerce(boolean commerce) {
        this.commerce = commerce;
    }

    public boolean isInvReSpawn() {
        return invReSpawn;
    }

    public void setInvReSpawn(boolean invReSpawn) {
        this.invReSpawn = invReSpawn;
    }

    public int getMovement() {
        return movement;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    public boolean isAttackable() {
        return attackable;
    }

    public void setAttackable(boolean attackable) {
        this.attackable = attackable;
    }

    public boolean isRespawn() {
        return respawn;
    }

    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    public boolean isDomable() {
        return domable;
    }

    public void setDomable(boolean domable) {
        this.domable = domable;
    }

    public boolean isAlignment() {
        return alignment;
    }

    public void setAlignment(boolean alignment) {
        this.alignment = alignment;
    }

    public boolean isBackup() {
        return backup;
    }

    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    public boolean isPoison() {
        return poison;
    }

    public void setPoison(boolean poison) {
        this.poison = poison;
    }

    public boolean isAffectParalysis() {
        return affectParalysis;
    }

    public void setAffectParalysis(boolean affectParalysis) {
        this.affectParalysis = affectParalysis;
    }

    public int getGiveEXP() {
        return giveEXP;
    }

    public void setGiveEXP(int giveEXP) {
        this.giveEXP = giveEXP;
    }

    public int getGiveGLD() {
        return giveGLD;
    }

    public void setGiveGLD(int giveGLD) {
        this.giveGLD = giveGLD;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getEvasionPower() {
        return evasionPower;
    }

    public void setEvasionPower(int evasionPower) {
        this.evasionPower = evasionPower;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public int getMinHP() {
        return minHP;
    }

    public void setMinHP(int minHP) {
        this.minHP = minHP;
    }

    public int getMaxHit() {
        return maxHit;
    }

    public void setMaxHit(int maxHit) {
        this.maxHit = maxHit;
    }

    public int getMinHit() {
        return minHit;
    }

    public void setMinHit(int minHit) {
        this.minHit = minHit;
    }

    public int getDef() {
        return def;
    }

    public void setDef(int def) {
        this.def = def;
    }

    public int getDefM() {
        return defM;
    }

    public void setDefM(int defM) {
        this.defM = defM;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDobleAttack() {
        return dobleAttack;
    }

    public void setDobleAttack(boolean dobleAttack) {
        this.dobleAttack = dobleAttack;
    }

    public boolean isFaction() {
        return faction;
    }

    public void setFaction(boolean faction) {
        this.faction = faction;
    }

    public boolean isInvalidEarth() {
        return invalidEarth;
    }

    public void setInvalidEarth(boolean invalidEarth) {
        this.invalidEarth = invalidEarth;
    }

    public boolean isValidWater() {
        return validWater;
    }

    public void setValidWater(boolean validWater) {
        this.validWater = validWater;
    }

    @Override
    public String toString() {
        return getId() + ": " + getName();
    }
}

