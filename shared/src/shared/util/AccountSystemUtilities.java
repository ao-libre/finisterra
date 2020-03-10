package shared.util;

import org.mindrot.jbcrypt.BCrypt;

import java.util.regex.Pattern;

//@todo la idea es tener una clase con utilidades para validar los campos, hashear, etc.

public class AccountSystemUtilities {

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

    /**
     * This method can be used to generate a string representing an account password
     * suitable for storing in a database. It will be an OpenBSD-style crypt(3) formatted
     * hash string of length=60
     * The BCrypt workload is specified in the above static variable, a value from 10 to 31.
     * A workload of 12 is a very reasonable safe default as of 2013.
     * This automatically handles secure 128-bit salt generation and storage within the hash.
     * @param password_plaintext The account's plaintext password as provided during account creation,
     *	or when changing an account's password.
     * @return String - a string of length 60 that is the BCrypt hashed password in crypt(3) format.
     */
    public static String hashPassword(String password_plaintext) {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);

        return(BCrypt.hashpw(password_plaintext, salt));
    }

    /**
     * This method can be used to verify a computed hash from a plaintext (e.g. during a login
     * request) with that of a stored hash from a database. The password hash from the database
     * must be passed as the second variable.
     * @param password_plaintext The account's plaintext password, as provided during a login request
     * @param stored_hash The account's stored password hash, retrieved from the authorization database
     * @return boolean - true if the password matches the password of the stored hash, false otherwise
     */
    public static boolean checkPassword(String password_plaintext, String stored_hash) {

        if(stored_hash == null || !stored_hash.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }

        return(BCrypt.checkpw(password_plaintext, stored_hash));
    }

}
