package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class Message {

    private int id;
    private String text, received_at;
    private Chat chat;
    private User user;

    public Message (int id, String text, Chat chat, User user, String received_at) {
        this.id = id;
        this.text = text;
        this.chat = chat;
        this.user = user;
        this.received_at = received_at;
    }

    public int getId() {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText (String text) {
        this.text = text;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat (Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return user;
    }

    public void setUser (User user) {
        this.user = user;
    }

    public String getReceived_at() {
        return received_at;
    }

    public void setReceived_at (String received_at) {
        this.received_at = received_at;
    }
}
