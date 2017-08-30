package mh.manager.models;

import java.io.Serializable;

/**
 * Created by DEV on 8/30/2017.
 */

public class ModelUrlRec implements Serializable{
    private String urlRec;

    public ModelUrlRec(){}

    public ModelUrlRec(String urlRec) {
        this.urlRec = urlRec;
    }

    public String getUrlRec() {
        return urlRec;
    }

    public void setUrlRec(String urlRec) {
        this.urlRec = urlRec;
    }
}
