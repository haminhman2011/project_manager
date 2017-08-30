package mh.manager.models;

import java.io.Serializable;

/**
 * Created by man.ha on 8/4/2017.
 */

public class ModelMyTicket implements Serializable{

    private String ticket_id;
    private String number = null;
    private String priority;
    private String username;
    private String email;
    private String department;
    private String departmentId;
    private String source;
    private String topicname;
    private String slaname;
    private String created;
    private String lastupdate;
    private String status;
    private String est_duedate;
    private String subject;
    private String phone;
    private String assigned_to;
    private String last_message;
    private String last_reponse;
    private String teamName;
    private String hotel;
    private String room;

    public ModelMyTicket(){}

    public ModelMyTicket(String ticket_id, String number, String priority, String username, String email, String department, String departmentId, String source, String topicname, String slaname, String created, String lastupdate, String status, String est_duedate, String subject, String phone, String assigned_to, String last_message, String last_reponse, String teamName, String hotel, String room) {
        this.ticket_id = ticket_id;
        this.number = number;
        this.priority = priority;
        this.username = username;
        this.email = email;
        this.department = department;
        this.departmentId = departmentId;
        this.source = source;
        this.topicname = topicname;
        this.slaname = slaname;
        this.created = created;
        this.lastupdate = lastupdate;
        this.status = status;
        this.est_duedate = est_duedate;
        this.subject = subject;
        this.phone = phone;
        this.assigned_to = assigned_to;
        this.last_message = last_message;
        this.last_reponse = last_reponse;
        this.teamName = teamName;
        this.hotel = hotel;
        this.room = room;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTopicname() {
        return topicname;
    }

    public void setTopicname(String topicname) {
        this.topicname = topicname;
    }

    public String getSlaname() {
        return slaname;
    }

    public void setSlaname(String slaname) {
        this.slaname = slaname;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEst_duedate() {
        return est_duedate;
    }

    public void setEst_duedate(String est_duedate) {
        this.est_duedate = est_duedate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAssigned_to() {
        return assigned_to;
    }

    public void setAssigned_to(String assigned_to) {
        this.assigned_to = assigned_to;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_reponse() {
        return last_reponse;
    }

    public void setLast_reponse(String last_reponse) {
        this.last_reponse = last_reponse;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
