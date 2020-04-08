package database;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.minlog.Log;
import server.database.Account;

/**
 * Aca testeamos el tiempo que le toma al servidor leer/escribir los .json que van a usar para guardar la info. de las cuentas.
 * 
 * Ejecutar desde {@link DatabaseTestLauncher#main(String[])}
 */
public class DatabaseTest implements ApplicationListener {

    @Override
    public void create() {
        Log.info("DatabaseTest application created");

        int n = 10000;
        String email[] = new String[n];
        for (int i = 0; i < n; i++) {
            email[i] = "email@domain.com" + i;
        }

        String email2[] = new String[n];
        for (int i = 0; i < n; i++) {
            email2[i] = "z-email@domain.com" + i;
        }

        long start, end;
        double runtime;

        /** Base de datos en disco, usando libgdx Json */
        Log.info("Base de datos en disco, usando libgdx Json.");

        /** Create test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account1 = new Account("", email[i], "aFeo3&5l2-+1w");
            account1.save();
        }
        end = System.currentTimeMillis();
        runtime = ((double)(end - start)) / n;
        Log.info( "Create test", "Average run time: " + runtime + " milliseconds.");

        /** Read test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account2 = Account.load(email[i]);
        }
        end = System.currentTimeMillis();
        runtime = ((double)(end - start)) / n;
        Log.info( "Read test", "Average run time: " + runtime + " milliseconds.");

        /** Update test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account1 = new Account("", email[i], "di3m1#0fwp32+");
            account1.save();
        }
        end = System.currentTimeMillis();
        runtime = ((double)(end - start)) / n;
        Log.info( "Update test", "Average run time: " + runtime + " milliseconds.");

        /** Worst-case find (non-existent entry) */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account.load(email2[i]);
        }
        end = System.currentTimeMillis();
        runtime = ((double)(end - start)) / n;
        Log.info( "Worst-case find test", "Average run time: " + runtime + " milliseconds.");

        /** null argument test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account.load(null);
        }
        end = System.currentTimeMillis();
        runtime = ((double)(end - start)) / n;
        Log.info( "null argument test", "Average run time: " + runtime + " milliseconds.");

        // Borramos todos los .json que se usaron en las pruebas.
        Gdx.files.local("Accounts").deleteDirectory();

        // Le decimos a la JVM que ya terminamos.
        System.exit(0);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}