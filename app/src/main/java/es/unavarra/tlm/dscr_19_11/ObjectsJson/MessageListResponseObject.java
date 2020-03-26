package es.unavarra.tlm.dscr_19_11.ObjectsJson;

import java.util.List;

public class MessageListResponseObject {

    private int count;
    private List<Message> messages;

    public MessageListResponseObject (int count, List<Message> messages) {
        this.count = count;
        this.messages = messages;
    }

    public int getCount() {
        return count;
    }

    public void setCount (int count) {
        this.count = count;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages (List<Message> messages) {
        this.messages = messages;
    }
}
