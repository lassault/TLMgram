package es.unavarra.tlm.dscr_19_11.ObjectsJson;

import java.util.ArrayList;

public class NewMessageResponseObject {
    private ArrayList<Message> messages;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public NewMessageResponseObject(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
