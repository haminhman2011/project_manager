package mh.manager.models;

import java.io.Serializable;

/**
 * Created by man.ha on 8/4/2017.
 */

public class ModelDynamicDetailMyTicket implements Serializable {
    private String label;
    private String value;
    private String type;
    private String entry_id;
    private String ticket_id;
    private String field_id;
    private String ticketStatus;

    public ModelDynamicDetailMyTicket(){}

    public ModelDynamicDetailMyTicket(String label, String value, String type, String entry_id, String ticket_id, String field_id, String ticketStatus) {
        this.label = label;
        this.value = value;
        this.type = type;
        this.entry_id = entry_id;
        this.ticket_id = ticket_id;
        this.field_id = field_id;
        this.ticketStatus = ticketStatus;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(String entry_id) {
        this.entry_id = entry_id;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public String getField_id() {
        return field_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}

