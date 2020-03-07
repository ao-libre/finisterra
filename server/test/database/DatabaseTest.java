package database;

import com.badlogic.gdx.ApplicationListener;
import com.esotericsoftware.minlog.Log;
import server.database.Account;

import java.util.concurrent.TimeUnit;

public class DatabaseTest implements ApplicationListener {

    @Override
    public void create() {
        Log.info("DatabaseTest application created");

        int n = 10000;
        String email[] = new String[10000];
        for (int i = 0; i < n; i++) {
            email[i] = "email@domain.com" + i;
        }

        long start, end;

        /** Base de datos en disco, usando libgdx Json */
        Log.info("Base de datos en disco, usando libgdx Json.");
        start = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            Account account1 = new Account(email[i], "aFeo3&5l2-+1w", "di3m1#0fwp32+");
            account1.save();
            Account account2 = Account.load(email[i]);
        }
        end = System.currentTimeMillis();
        Log.info( "Elapsed time: " + (end - start) + " milliseconds.");
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