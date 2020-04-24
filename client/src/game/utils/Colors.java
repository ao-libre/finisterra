package game.utils;

import com.badlogic.gdx.graphics.Color;
import component.entity.world.CombatMessage;

public class Colors {

    public static final Color MANA = new Color((float) 27 / 255, (float) 156 / 255, (float) 252 / 255, 1);
    public static final Color HEALTH = new Color((float) 253 / 255, (float) 114 / 255, (float) 114 / 255, 1);
    public static final Color EXP = new Color((float) 99 / 255, (float) 110 / 255, (float) 114 / 255, 1);

    public static final Color GM = rgb(46, 204, 113);
    public static final Color NEWBIE = rgb(155, 89, 182);
    public static final Color CITIZEN = rgb(52, 152, 219);
    public static final Color CRIMINAL = rgb(231, 76, 60);
    public static final Color COMBAT = rgb(255, 76, 60);
    public static final Color GREY = rgb(149, 165, 166);
    public static final Color TRANSPARENT_RED = rgba(231, 76, 60, 0.1f);
    public static final Color RED = rgb(231, 76, 60);
    public static final Color YELLOW = rgb(244, 244, 143);

    public static Color rgba(int r, int g, int b, float a) {
        return new Color((float) r / 255, (float) g / 255, (float) b / 255, a);
    }


    private static Color rgb(int r, int g, int b) {
        return rgba(r, g, b, 1);
    }

    public static Color get(CombatMessage message) {
        Color color = Color.WHITE.cpy();
        switch (message.kind) {
            case MAGIC:
                color = Colors.MANA.cpy();
                break;
            case STAB:
                color = Colors.GREY.cpy();
                break;
            case ENERGY:
                color = Colors.YELLOW.cpy();
                break;
            case PHYSICAL:
                color = Colors.RED.cpy();
                break;
        }

        return color;
    }
}
