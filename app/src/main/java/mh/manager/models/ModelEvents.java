package mh.manager.models;

import java.io.Serializable;

/**
 * Created by man.ha on 8/2/2017.
 */

public class ModelEvents implements Serializable{
    public String id = null;
    public String thread_id = null;
    public String staff_id = null;
    public String team_id = null;
    public String dept_id = null;
    public String topic_id = null;
    public String state = null;
    public String data = null;
    public String username = null;
    public String uid = null;
    public String uid_type = null;
    public String annulled = null;
    public String timestamp = null;
    public String firstname = null;
    public String lastname = null;
    public String messageText = null;

    public ModelEvents(){}

    public ModelEvents(String id, String thread_id, String staff_id, String team_id, String dept_id, String topic_id, String state, String data, String username, String uid, String uid_type, String annulled, String timestamp, String firstname, String lastname, String messageText) {
        this.id = id;
        this.thread_id = thread_id;
        this.staff_id = staff_id;
        this.team_id = team_id;
        this.dept_id = dept_id;
        this.topic_id = topic_id;
        this.state = state;
        this.data = data;
        this.username = username;
        this.uid = uid;
        this.uid_type = uid_type;
        this.annulled = annulled;
        this.timestamp = timestamp;
        this.firstname = firstname;
        this.lastname = lastname;
        this.messageText = messageText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThread_id() {
        return thread_id;
    }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public String getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(String topic_id) {
        this.topic_id = topic_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid_type() {
        return uid_type;
    }

    public void setUid_type(String uid_type) {
        this.uid_type = uid_type;
    }

    public String getAnnulled() {
        return annulled;
    }

    public void setAnnulled(String annulled) {
        this.annulled = annulled;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
