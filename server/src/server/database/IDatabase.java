package server.database;

import server.database.model.User;

public interface IDatabase {

    User findUser(String username);

}
