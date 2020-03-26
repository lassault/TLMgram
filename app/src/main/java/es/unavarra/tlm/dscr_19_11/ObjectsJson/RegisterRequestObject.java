package es.unavarra.tlm.dscr_19_11.ObjectsJson;

public class RegisterRequestObject {

    private String email, password, name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RegisterRequestObject(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
}
