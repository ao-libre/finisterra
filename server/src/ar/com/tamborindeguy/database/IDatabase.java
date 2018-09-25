package ar.com.tamborindeguy.database;

import ar.com.tamborindeguy.database.model.User;

public interface IDatabase {

    User findUser(String username);

}
