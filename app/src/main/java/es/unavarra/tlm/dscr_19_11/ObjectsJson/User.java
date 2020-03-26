package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class User {
    private String email, name;
    private int id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User(String email, String name, int id) {
        this.email = email;
        this.name = name;
        this.id = id;
    }
}
