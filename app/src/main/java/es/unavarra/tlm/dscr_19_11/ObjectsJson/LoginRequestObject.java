package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class LoginRequestObject {
    private String email, password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginRequestObject(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
