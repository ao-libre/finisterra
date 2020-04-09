package game.utils;

import com.badlogic.gdx.Input;

/**
 * Aca se setea la distribucion de las teclas alternativa del juego.
 *
 * @see game.managers.AOInputProcessor#keyUp(int) activa/desactiva teclas alternativas.
 * @see com.badlogic.gdx.Input.Keys para ver las constantes que representan las teclas en LibGDX.
 */
public class AlternativeKeys {

    public static final int ATTACK_1 = Input.Keys.SPACE;
    public static final int ATTACK_2 = Input.Keys.SHIFT_RIGHT;
    public static final int MEDITATE = Input.Keys.M;
    public static final int USE = Input.Keys.SHIFT_LEFT;
    //public static final int HIDE = Input.Keys.O;
    public static final int INVENTORY = Input.Keys.I;
    public static final int SPELLS = Input.Keys.K;
    public static final int TALK = Input.Keys.ENTER;
    public static final int DROP = Input.Keys.T;
    public static final int TAKE = Input.Keys.L;
    public static final int EQUIP = Input.Keys.E;

    public static int MOVE_LEFT = Input.Keys.A;
    public static int MOVE_RIGHT = Input.Keys.D;
    public static int MOVE_UP = Input.Keys.W;
    public static int MOVE_DOWN = Input.Keys.S;


}
