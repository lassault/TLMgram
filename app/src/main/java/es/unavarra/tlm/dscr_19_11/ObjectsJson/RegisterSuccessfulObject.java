package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class RegisterSuccessfulObject {
    private Session session;
    private User user;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public RegisterSuccessfulObject(Session session, User user) {
        this.session = session;
        this.user = user;
    }
}
