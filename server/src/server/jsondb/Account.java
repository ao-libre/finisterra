package server.jsondb;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;

/**
 * A test Pojo representing a imaginary class Instance.
 * @version 1.0 28-Sep-2016
 */
@Document(collection = "accounts", schemaVersion= "1.0")
public class Account {
    //This field will be used as a primary key, every POJO should have one
    @Id
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}