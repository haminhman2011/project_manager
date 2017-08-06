package mh.manager.models;

import java.io.Serializable;

/**
 * Created by man.ha on 7/28/2017.
 */

public class ModelTeam implements Serializable{
    public String team_id;
    public String lead_id;
    public String flags;
    public String name;
    public String notes;
    public String created;
    public String updated;

    public ModelTeam(){}

    public ModelTeam(String team_id, String lead_id, String flags, String name, String notes, String created, String updated) {
        this.team_id = team_id;
        this.lead_id = lead_id;
        this.flags = flags;
        this.name = name;
        this.notes = notes;
        this.created = created;
        this.updated = updated;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getLead_id() {
        return lead_id;
    }

    public void setLead_id(String lead_id) {
        this.lead_id = lead_id;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }
}
