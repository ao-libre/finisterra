package game.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public enum SawRecipes {
    None(0,0,0,0),
    Newbie_arrow(10,136,10,860 );

    public static final List< SawRecipes > VALUES = List.copyOf( Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private final int needCount, needObjID, resultCount, resultObjID;

    SawRecipes(int needCount, int needObjID, int resultCount, int resultObjID){
        this.needCount = needCount;
        this.needObjID = needObjID;
        this.resultCount = resultCount;
        this.resultObjID = resultObjID;
    }

    public static List<SawRecipes> getSawRecipes() {
        return VALUES.stream().collect( Collectors.toList());
    }

    public int getNeedCount() {
        return needCount;
    }

    public int getNeedObjID() {
        return needObjID;
    }

    public int getResultCount() {
        return resultCount;
    }

    public int getResultObjID() {
        return resultObjID;
    }
}
