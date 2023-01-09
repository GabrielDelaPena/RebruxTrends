package be.ehb.rebruxdef.models;

public class User {
    String username, email, phone;
    int points;

    public User() {
    }

    public User(String username, String email, String phone, int points) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.points = points;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
