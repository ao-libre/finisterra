package shared.network.account;

import java.util.regex.Pattern;

//@todo la idea es tener una clase con utilidades para validar los campos, hashear, etc.
//@todo shared.util quizá sea un paquete más apropiado

public class AccountValidator {
    public static boolean validateAccount(String username, String email, String password) {
        return validateUsername(username) && validateEmail(email) && validatePassword(password);
    }

    public static boolean validateAccount(String username, String email, String hash, String salt) {
        return validateUsername(username) && validateEmail(email) /*&& validateHash(hash, salt)*/;
    }

    public static boolean validateUsername(String username) {
        return Pattern.matches("([a-zA-Z0-9_]){4,20}", username); //@todo agregar soporte para caracteres latinos
    }

    /** @see "http://www.regular-expressions.info/email.html" */
    public static boolean validateEmail(String email) {
        return Pattern.matches("(\\w){4,20}", email);
    }

    public static boolean validatePassword(String password) {
        return Pattern.matches("(\\w){4,20}", password);
    }
}
