package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class Session {
    private String token, valid_until;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public Session(String token, String valid_until) {
        this.token = token;
        this.valid_until = valid_until;
    }
}
