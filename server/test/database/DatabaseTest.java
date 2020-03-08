package database;

import com.badlogic.gdx.ApplicationListener;
import com.esotericsoftware.minlog.Log;
import server.database.Account;

import java.util.concurrent.TimeUnit;

public class DatabaseTest implements ApplicationListener {

    @Override
    public void create() {
        Log.info("DatabaseTest application created");

        int n = 100000;
        String email[] = new String[n];
        for (int i = 0; i < n; i++) {
            email[i] = "email@domain.com" + i;
        }

        String email2[] = new String[n];
        for (int i = 0; i < n; i++) {
            email2[i] = "z-email@domain.com" + i;
        }

        long start, end;

        /** Base de datos en disco, usando libgdx Json */
        Log.info("Base de datos en disco, usando libgdx Json.");

        /** Create test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account1 = new Account(email[i], "aFeo3&5l2-+1w", "di3m1#0fwp32+");
            account1.save();
        }
        end = System.currentTimeMillis();
        Log.info( "Create test", "Elapsed time: " + (end - start) + " milliseconds.");

        /** Read test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account2 = Account.load(email[i]);
        }
        end = System.currentTimeMillis();
        Log.info( "Read test", "Elapsed time: " + (end - start) + " milliseconds.");

        /** Update test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account1 = new Account(email[i], "di3m1#0fwp32+", "aFeo3&5l2-+1w");
            account1.save();
        }
        end = System.currentTimeMillis();
        Log.info( "Update test", "Elapsed time: " + (end - start) + " milliseconds.");

        /** Worst-case find (non-existent entry) */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account.load(email2[i]);
        }
        end = System.currentTimeMillis();
        Log.info( "Worst-case find test", "Elapsed time: " + (end - start) + " milliseconds.");

        /** null argument test */
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account.load(null);
        }
        end = System.currentTimeMillis();
        Log.info( "null argument test", "Elapsed time: " + (end - start) + " milliseconds.");
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