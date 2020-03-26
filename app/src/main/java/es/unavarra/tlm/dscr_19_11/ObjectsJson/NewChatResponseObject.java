package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class NewChatResponseObject {

    private Chat chat;

    public NewChatResponseObject(Chat chat){
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
