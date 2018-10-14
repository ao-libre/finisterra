package ar.com.tamborindeguy.model;

public class Spell {
    String name;
    String desc;
    String magicWords;
    String originMsg;
    String ownerMsg;
    String targetMsg;
    int type;
    int wav;
    int fxGrh;
    int loops;
    int minSkill;
    int requiredMana;
    int requiredStamina;
    int target;

    // HP
    boolean sumHP;
    int minHP;
    int maxHP;

    // MANA
    boolean sumMana;
    int maxMana;
    int minMana;

    // STAMINA
    boolean sumStamina;
    int minSta;
    int maxSta;

    // HANGRY
    boolean sumHangry;
    int minHangry;
    int maxHangry;

    // THIRSTY
    boolean sumThirsty;
    int minThirsty;
    int maxThirsty;

    // AGILITY
    boolean sumAgility;
    int minAgility;
    int maxAgility;

    // STRENGTH
    boolean sumStrength;
    int minStrength;
    int maxStrength;

    // CA?
    boolean sumCA;
    int minCA;
    int maxCA;

    boolean invisibility;
    boolean paralyze;
    boolean immobilize;

    boolean removeParalysis;
    boolean removeStupid;
    boolean removeParcialInvisibility;
    boolean healPoison;
    boolean poison;
    boolean revive;
    boolean blindness;
    boolean stupid;

    // INVOKE
    boolean invokes;
    int numNpc;
    int count;

    boolean mimetize;
    boolean materialize;

    int itemIndex;
    boolean staffAffected;
    boolean needStaff;
    boolean resis;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMagicWords() {
        return magicWords;
    }

    public void setMagicWords(String magicWords) {
        this.magicWords = magicWords;
    }

    public String getOriginMsg() {
        return originMsg;
    }

    public void setOriginMsg(String originMsg) {
        this.originMsg = originMsg;
    }

    public String getOwnerMsg() {
        return ownerMsg;
    }

    public void setOwnerMsg(String ownerMsg) {
        this.ownerMsg = ownerMsg;
    }

    public String getTargetMsg() {
        return targetMsg;
    }

    public void setTargetMsg(String targeMsg) {
        this.targetMsg = targeMsg;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWav() {
        return wav;
    }

    public void setWav(int wav) {
        this.wav = wav;
    }

    public int getFxGrh() {
        return fxGrh;
    }

    public void setFxGrh(int fxGrh) {
        this.fxGrh = fxGrh;
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(int loops) {
        this.loops = loops;
    }

    public int getMinSkill() {
        return minSkill;
    }

    public void setMinSkill(int minSkill) {
        this.minSkill = minSkill;
    }

    public int getRequiredMana() {
        return requiredMana;
    }

    public void setRequiredMana(int requiredMana) {
        this.requiredMana = requiredMana;
    }

    public int getRequiredStamina() {
        return requiredStamina;
    }

    public void setRequiredStamina(int requiredStamina) {
        this.requiredStamina = requiredStamina;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public boolean isSumHP() {
        return sumHP;
    }

    public void setSumHP(boolean sumHP) {
        this.sumHP = sumHP;
    }

    public int getMinHP() {
        return minHP;
    }

    public void setMinHP(int minHP) {
        this.minHP = minHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int maxHP) {
        this.maxHP = maxHP;
    }

    public boolean isSumMana() {
        return sumMana;
    }

    public void setSumMana(boolean sumMana) {
        this.sumMana = sumMana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public int getMinMana() {
        return minMana;
    }

    public void setMinMana(int minMana) {
        this.minMana = minMana;
    }

    public boolean isSumStamina() {
        return sumStamina;
    }

    public void setSumStamina(boolean sumStamina) {
        this.sumStamina = sumStamina;
    }

    public int getMinSta() {
        return minSta;
    }

    public void setMinSta(int minSta) {
        this.minSta = minSta;
    }

    public int getMaxSta() {
        return maxSta;
    }

    public void setMaxSta(int maxSta) {
        this.maxSta = maxSta;
    }

    public boolean isSumHangry() {
        return sumHangry;
    }

    public void setSumHangry(boolean sumHangry) {
        this.sumHangry = sumHangry;
    }

    public int getMinHangry() {
        return minHangry;
    }

    public void setMinHangry(int minHangry) {
        this.minHangry = minHangry;
    }

    public int getMaxHangry() {
        return maxHangry;
    }

    public void setMaxHangry(int maxHangry) {
        this.maxHangry = maxHangry;
    }

    public boolean isSumThirsty() {
        return sumThirsty;
    }

    public void setSumThirsty(boolean sumThirsty) {
        this.sumThirsty = sumThirsty;
    }

    public int getMinThirsty() {
        return minThirsty;
    }

    public void setMinThirsty(int minThirsty) {
        this.minThirsty = minThirsty;
    }

    public int getMaxThirsty() {
        return maxThirsty;
    }

    public void setMaxThirsty(int maxThirsty) {
        this.maxThirsty = maxThirsty;
    }

    public boolean isSumAgility() {
        return sumAgility;
    }

    public void setSumAgility(boolean sumAgility) {
        this.sumAgility = sumAgility;
    }

    public int getMinAgility() {
        return minAgility;
    }

    public void setMinAgility(int minAgility) {
        this.minAgility = minAgility;
    }

    public int getMaxAgility() {
        return maxAgility;
    }

    public void setMaxAgility(int maxAgility) {
        this.maxAgility = maxAgility;
    }

    public boolean isSumStrength() {
        return sumStrength;
    }

    public void setSumStrength(boolean sumStrength) {
        this.sumStrength = sumStrength;
    }

    public int getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(int minStrength) {
        this.minStrength = minStrength;
    }

    public int getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(int maxStrength) {
        this.maxStrength = maxStrength;
    }

    public boolean isSumCA() {
        return sumCA;
    }

    public void setSumCA(boolean sumCA) {
        this.sumCA = sumCA;
    }

    public int getMinCA() {
        return minCA;
    }

    public void setMinCA(int minCA) {
        this.minCA = minCA;
    }

    public int getMaxCA() {
        return maxCA;
    }

    public void setMaxCA(int maxCA) {
        this.maxCA = maxCA;
    }

    public boolean isInvisibility() {
        return invisibility;
    }

    public void setInvisibility(boolean invisibility) {
        this.invisibility = invisibility;
    }

    public boolean isParalyze() {
        return paralyze;
    }

    public void setParalyze(boolean paralyze) {
        this.paralyze = paralyze;
    }

    public boolean isImmobilize() {
        return immobilize;
    }

    public void setImmobilize(boolean immobilize) {
        this.immobilize = immobilize;
    }

    public boolean isRemoveParalysis() {
        return removeParalysis;
    }

    public void setRemoveParalysis(boolean removeParalysis) {
        this.removeParalysis = removeParalysis;
    }

    public boolean isRemoveStupid() {
        return removeStupid;
    }

    public void setRemoveStupid(boolean removeStupid) {
        this.removeStupid = removeStupid;
    }

    public boolean isRemoveParcialInvisibility() {
        return removeParcialInvisibility;
    }

    public void setRemoveParcialInvisibility(boolean removeParcialInvisibility) {
        this.removeParcialInvisibility = removeParcialInvisibility;
    }

    public boolean isHealPoison() {
        return healPoison;
    }

    public void setHealPoison(boolean healPoison) {
        this.healPoison = healPoison;
    }

    public boolean isPoison() {
        return poison;
    }

    public void setPoison(boolean poison) {
        this.poison = poison;
    }

    public boolean isRevive() {
        return revive;
    }

    public void setRevive(boolean revive) {
        this.revive = revive;
    }

    public boolean isBlindness() {
        return blindness;
    }

    public void setBlindness(boolean blindness) {
        this.blindness = blindness;
    }

    public boolean isStupid() {
        return stupid;
    }

    public void setStupid(boolean stupid) {
        this.stupid = stupid;
    }

    public boolean isInvokes() {
        return invokes;
    }

    public void setInvokes(boolean invokes) {
        this.invokes = invokes;
    }

    public int getNumNpc() {
        return numNpc;
    }

    public void setNumNpc(int numNpc) {
        this.numNpc = numNpc;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isMimetize() {
        return mimetize;
    }

    public void setMimetize(boolean mimetize) {
        this.mimetize = mimetize;
    }

    public boolean isMaterialize() {
        return materialize;
    }

    public void setMaterialize(boolean materialize) {
        this.materialize = materialize;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(int itemIndex) {
        this.itemIndex = itemIndex;
    }

    public boolean isStaffAffected() {
        return staffAffected;
    }

    public void setStaffAffected(boolean staffAffected) {
        this.staffAffected = staffAffected;
    }

    public boolean isNeedStaff() {
        return needStaff;
    }

    public void setNeedStaff(boolean needStaff) {
        this.needStaff = needStaff;
    }

    public boolean isResis() {
        return resis;
    }

    public void setResis(boolean resis) {
        this.resis = resis;
    }
}
