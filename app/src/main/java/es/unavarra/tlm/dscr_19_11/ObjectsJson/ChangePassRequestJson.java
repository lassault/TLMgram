package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class ChangePassRequestJson {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ChangePassRequestJson(String password) {
        this.password = password;
    }
}
