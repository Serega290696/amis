package belcer.remoteserverconnector.model.entity;

import java.sql.Date;
import java.sql.Timestamp;

public class User {

    private String username;
    private String email;
    private String password;
    private Timestamp registrationDate;
    private Timestamp lastLogin;
    private Role role;
    private byte deleted;
    private byte banned;

    public User(String username, String email, String password, Role role) {
        this(username, email, password, new Timestamp(System.currentTimeMillis()), null, role);
    }

    public User(String username, String email, String password, Timestamp registrationDate, Timestamp lastLogin, Role role,
                int deleted, int banned) {
//    System.out.println("New user cteated: " + username + ", " + email + ", " + password);
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.role = role;
        this.deleted = (byte) deleted;
        this.banned = (byte) banned;
    }

    public User(String username, String email, String password, Timestamp registrationDate, Timestamp lastLogin, Role role) {
//    System.out.println("New user cteated: " + username + ", " + email + ", " + password);
        this.username = username;
        this.email = email;
        this.password = password;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.role = role;
        this.deleted = 0;
        this.banned = 0;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public Timestamp getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Timestamp registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public byte getDeleted() {
        return deleted;
    }

    public void setDeleted(byte deleted) {
        this.deleted = deleted;
    }

    public byte getBanned() {
        return banned;
    }

    public void setBanned(byte banned) {
        this.banned = banned;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", registrationDate=" + registrationDate +
                ", lastLogin=" + lastLogin +
                ", role=" + role +
                ", deleted=" + deleted +
                ", banned=" + banned +
                '}';
    }
}
