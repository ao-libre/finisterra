package shared.model;

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
    int sumHP;
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
    float agilityDuration;

    // STRENGTH
    boolean sumStrength;
    int minStrength;
    int maxStrength;
    float strengthDuration;

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

    public void setType(String type) {
        this.type = Integer.parseInt(type);
    }

    public int getWav() {
        return wav;
    }

    public void setWav(String wav) {
        this.wav = Integer.parseInt(wav);
    }

    public int getFxGrh() {
        return fxGrh;
    }

    public void setFxGrh(String fxGrh) {
        this.fxGrh = Integer.parseInt(fxGrh);
    }

    public int getLoops() {
        return loops;
    }

    public void setLoops(String loops) {
        this.loops = Integer.parseInt(loops);
    }

    public int getMinSkill() {
        return minSkill;
    }

    public void setMinSkill(String minSkill) {
        this.minSkill = Integer.parseInt(minSkill);
    }

    public int getRequiredMana() {
        return requiredMana;
    }

    public void setRequiredMana(String requiredMana) {
        this.requiredMana = Integer.parseInt(requiredMana);
    }

    public int getRequiredStamina() {
        return requiredStamina;
    }

    public void setRequiredStamina(String requiredStamina) {
        this.requiredStamina = Integer.parseInt(requiredStamina);
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = Integer.parseInt(target);
    }

    public int getSumHP() {
        return sumHP;
    }

    public void setSumHP(String sumHP) {
        this.sumHP = Integer.parseInt(sumHP);
    }

    public int getMinHP() {
        return minHP;
    }

    public void setMinHP(String minHP) {
        this.minHP = Integer.parseInt(minHP);
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(String maxHP) {
        this.maxHP = Integer.parseInt(maxHP);
    }

    public boolean isSumMana() {
        return sumMana;
    }

    public void setSumMana(String sumMana) {
        this.sumMana = sumMana.equals("1");
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(String maxMana) {
        this.maxMana = Integer.parseInt(maxMana);
    }

    public int getMinMana() {
        return minMana;
    }

    public void setMinMana(String minMana) {
        this.minMana = Integer.parseInt(minMana);
    }

    public boolean isSumStamina() {
        return sumStamina;
    }

    public void setSumStamina(String sumStamina) {
        this.sumStamina = sumStamina.equals("1");
    }

    public int getMinSta() {
        return minSta;
    }

    public void setMinSta(String minSta) {
        this.minSta = Integer.parseInt(minSta);
    }

    public int getMaxSta() {
        return maxSta;
    }

    public void setMaxSta(String maxSta) {
        this.maxSta = Integer.parseInt(maxSta);
    }

    public boolean isSumHangry() {
        return sumHangry;
    }

    public void setSumHangry(String sumHangry) {
        this.sumHangry = sumHangry.equals("1");
    }

    public int getMinHangry() {
        return minHangry;
    }

    public void setMinHangry(String minHangry) {
        this.minHangry = Integer.parseInt(minHangry);
    }

    public int getMaxHangry() {
        return maxHangry;
    }

    public void setMaxHangry(String maxHangry) {
        this.maxHangry = Integer.parseInt(maxHangry);
    }

    public boolean isSumThirsty() {
        return sumThirsty;
    }

    public void setSumThirsty(String sumThirsty) {
        this.sumThirsty = sumThirsty.equals("1");
    }

    public int getMinThirsty() {
        return minThirsty;
    }

    public void setMinThirsty(String minThirsty) {
        this.minThirsty = Integer.parseInt(minThirsty);
    }

    public int getMaxThirsty() {
        return maxThirsty;
    }

    public void setMaxThirsty(String maxThirsty) {
        this.maxThirsty = Integer.parseInt(maxThirsty);
    }

    public boolean isSumAgility() {
        return sumAgility;
    }

    public void setSumAgility(String sumAgility) {
        this.sumAgility = sumAgility.equals("1");
    }

    public int getMinAgility() {
        return minAgility;
    }

    public void setMinAgility(String minAgility) {
        this.minAgility = Integer.parseInt(minAgility);
    }

    public int getMaxAgility() {
        return maxAgility;
    }

    public void setMaxAgility(String maxAgility) {
        this.maxAgility = Integer.parseInt(maxAgility);
    }

    public boolean isSumStrength() {
        return sumStrength;
    }

    public void setSumStrength(String sumStrength) {
        this.sumStrength = sumStrength.equals("1");
    }

    public int getMinStrength() {
        return minStrength;
    }

    public void setMinStrength(String minStrength) {
        this.minStrength = Integer.parseInt(minStrength);
    }

    public int getMaxStrength() {
        return maxStrength;
    }

    public void setMaxStrength(String maxStrength) {
        this.maxStrength = Integer.parseInt(maxStrength);
    }

    public boolean isSumCA() {
        return sumCA;
    }

    public void setSumCA(String sumCA) {
        this.sumCA = sumCA.equals("1");
    }

    public int getMinCA() {
        return minCA;
    }

    public void setMinCA(String minCA) {
        this.minCA = Integer.parseInt(minCA);
    }

    public int getMaxCA() {
        return maxCA;
    }

    public void setMaxCA(String maxCA) {
        this.maxCA = Integer.parseInt(maxCA);
    }

    public boolean isInvisibility() {
        return invisibility;
    }

    public void setInvisibility(String invisibility) {
        this.invisibility = invisibility.equals("1");
    }

    public boolean isParalyze() {
        return paralyze;
    }

    public void setParalyze(String paralyze) {
        this.paralyze = paralyze.equals("1");
    }

    public boolean isImmobilize() {
        return immobilize;
    }

    public void setImmobilize(String immobilize) {
        this.immobilize = immobilize.equals("1");
    }

    public boolean isRemoveParalysis() {
        return removeParalysis;
    }

    public void setRemoveParalysis(String removeParalysis) {
        this.removeParalysis = removeParalysis.equals("1");
    }

    public boolean isRemoveStupid() {
        return removeStupid;
    }

    public void setRemoveStupid(String removeStupid) {
        this.removeStupid = removeStupid.equals("1");
    }

    public boolean isRemoveParcialInvisibility() {
        return removeParcialInvisibility;
    }

    public void setRemoveParcialInvisibility(String removeParcialInvisibility) {
        this.removeParcialInvisibility = removeParcialInvisibility.equals("1");
    }

    public boolean isHealPoison() {
        return healPoison;
    }

    public void setHealPoison(String healPoison) {
        this.healPoison = healPoison.equals("1");
    }

    public boolean isPoison() {
        return poison;
    }

    public void setPoison(String poison) {
        this.poison = poison.equals("1");
    }

    public boolean isRevive() {
        return revive;
    }

    public void setRevive(String revive) {
        this.revive = revive.equals("1");
    }

    public boolean isBlindness() {
        return blindness;
    }

    public void setBlindness(String blindness) {
        this.blindness = blindness.equals("1");
    }

    public boolean isStupid() {
        return stupid;
    }

    public void setStupid(String stupid) {
        this.stupid = stupid.equals("1");
    }

    public boolean isInvokes() {
        return invokes;
    }

    public void setInvokes(String invokes) {
        this.invokes = invokes.equals("1");
    }

    public int getNumNpc() {
        return numNpc;
    }

    public void setNumNpc(String numNpc) {
        this.numNpc = Integer.parseInt(numNpc);
    }

    public int getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = Integer.parseInt(count);
    }

    public boolean isMimetize() {
        return mimetize;
    }

    public void setMimetize(String mimetize) {
        this.mimetize = mimetize.equals("1");
    }

    public boolean isMaterialize() {
        return materialize;
    }

    public void setMaterialize(String materialize) {
        this.materialize = materialize.equals("1");
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex(String itemIndex) {
        this.itemIndex = Integer.parseInt(itemIndex);
    }

    public boolean isStaffAffected() {
        return staffAffected;
    }

    public void setStaffAffected(String staffAffected) {
        this.staffAffected = staffAffected.equals("1");
    }

    public boolean isNeedStaff() {
        return needStaff;
    }

    public void setNeedStaff(String needStaff) {
        this.needStaff = needStaff.equals("1");
    }

    public boolean isResis() {
        return resis;
    }

    public void setResis(String resis) {
        this.resis = resis.equals("1");
    }

    public float getAgilityDuration() {
        return agilityDuration;
    }

    public void setAgilityDuration(String agDur) {
        this.agilityDuration = Float.parseFloat(agDur);
    }

    public float getStrengthDuration() {
        return strengthDuration;
    }

    public void setStrengthDuration(String strDur) {
        this.strengthDuration = Float.parseFloat(strDur);
    }

    @Override
    public String toString() {
        return getName();
    }
}
