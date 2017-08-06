package mh.manager.models;

import java.io.Serializable;

/**
 * Created by man.ha on 7/27/2017.
 */

public class ModelStatus implements Serializable {
    private String id;
    private String name;
    private String state;

    public ModelStatus(){}

    public ModelStatus(String id, String name, String state) {
        this.id = id;
        this.name = name;
        this.state =state;
    }

    public ModelStatus(int i, String chooose) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

