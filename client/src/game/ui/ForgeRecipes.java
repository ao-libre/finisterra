package game.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum ForgeRecipes {
    None(0,0,0,0,0,0,0,0,0);


    public static final List< ForgeRecipes > VALUES = List.copyOf( Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private final int needCount, needObjID, needCount2, needObjID2, needCount3, needObjID3, resultCount, resultObjID
            , recipeLvl;

    ForgeRecipes(int needCount, int needObjID, int needCount2, int needObjID2, int needCount3, int needObjID3
            , int resultCount, int resultObjID, int recipeLvl){
        this.needCount = needCount;
        this.needObjID = needObjID;
        this.needCount2 = needCount2;
        this.needObjID2 = needObjID2;
        this.needCount3 = needCount3;
        this.needObjID3 = needObjID3;
        this.resultCount = resultCount;
        this.resultObjID = resultObjID;
        this.recipeLvl = recipeLvl;
    }

    public static List< ForgeRecipes > getForgeRecipes() {
        return VALUES.stream().collect( Collectors.toList());
    }

    public int getNeedCount() {
        return needCount;
    }

    public int getNeedObjID() {
        return needObjID;
    }

    public int getNeedCount2() {
        return needCount2;
    }

    public int getNeedObjID2() {
        return needObjID2;
    }

    public int getNeedCount3() {
        return needCount3;
    }

    public int getNeedObjID3() {
        return needObjID3;
    }

    public int getResultCount() {
        return resultCount;
    }

    public int getResultObjID() {
        return resultObjID;
    }

    public int getRecipeLvl() {
        return recipeLvl;
    }
}
