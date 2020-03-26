package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class ProfileResponseJson {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ProfileResponseJson(User user) {
        this.user = user;
    }
}
