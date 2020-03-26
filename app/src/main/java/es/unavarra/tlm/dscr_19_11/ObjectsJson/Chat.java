package es.unavarra.tlm.dscr_19_11.ObjectsJson;

import java.util.ArrayList;

public class Chat {
    private int id, participants;
    private String created_at;
    private ArrayList<User> users;

    public Chat (int id, int participants, String created_at, ArrayList<User> users){
        this.id=id;
        this.participants = participants;
        this.created_at = created_at;
        this.users = users;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
