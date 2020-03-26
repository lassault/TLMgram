package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class Notification {

    private String sender, type, when, message;
    private int chat, sender_id;


    public Notification (String sender, int chat, String type, String when, int sender_id, String message) {
        this.sender = sender;
        this.chat = chat;
        this.type = type;
        this.when = when;
        this.sender_id = sender_id;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public int getChat() {
        return chat;
    }

    public String getType() {
        return type;
    }

    public String getWhen() {
        return when;
    }

    public int getSender_id() {
        return sender_id;
    }

    public String getMessage() {
        return message;
    }

}
