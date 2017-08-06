package mh.manager.models;

import java.io.Serializable;

/**
 * Created by man.ha on 7/28/2017.
 */

public class ModelAgent implements Serializable{
    public String staff_id;
    public String username;
    public String firstname;
    public String lastname;

    public ModelAgent(){}

    public ModelAgent(String staff_id, String username, String firstname, String lastname) {
        this.staff_id = staff_id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
