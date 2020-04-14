package game.utils;

import com.badlogic.gdx.Input;

/**
 * Aca se setea la distribucion de las teclas del juego.

 * @see com.badlogic.gdx.Input.Keys para ver las constantes que representan las teclas en LibGDX.
 */
public class AOKeys {

    public static final int ATTACK_1 = Input.Keys.CONTROL_LEFT;
    public static final int ATTACK_2 = Input.Keys.CONTROL_RIGHT;
    public static final int MEDITATE = Input.Keys.M;
    public static final int USE = Input.Keys.U;
    //public static final int HIDE = Input.Keys.O;
    public static final int INVENTORY = Input.Keys.I;
    public static final int SPELLS = Input.Keys.K;
    public static final int TALK = Input.Keys.ENTER;
    public static final int DROP = Input.Keys.T;
    public static final int TAKE = Input.Keys.A;
    public static final int EQUIP = Input.Keys.E;

    public static int MOVE_LEFT = Input.Keys.LEFT;
    public static int MOVE_RIGHT = Input.Keys.RIGHT;
    public static int MOVE_UP = Input.Keys.UP;
    public static int MOVE_DOWN = Input.Keys.DOWN;

}
