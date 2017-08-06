package mh.manager;

/**
 * Created by man.ha on 7/27/2017.
 */

public class LoginModel {
    private String username;
    private String password;

    public LoginModel(){}

    public LoginModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
