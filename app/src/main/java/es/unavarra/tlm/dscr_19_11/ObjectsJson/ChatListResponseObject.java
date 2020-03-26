package es.unavarra.tlm.dscr_19_11.ObjectsJson;

import java.util.ArrayList;
import java.util.List;

public class ChatListResponseObject {

    private int count;
    private ArrayList<Chat> chats;

    public ChatListResponseObject (int count, ArrayList<Chat> chats) {
        this.count = count;
        this.chats = chats;
    }

    public int getCount() {
        return count;
    }

    public void setCount (int count) {
        this.count = count;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public void setChats (ArrayList<Chat> chats) {
        this.chats = chats;
    }
}
