package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@JsonAutoDetect
public class User implements Serializable,Comparable<User> {
    private String login;
    private String firstName;
    private String secondName;
    private String email;
    private String role;
    private String password;

    public User(String login, String firstName, String secondName, String email, String role, String password) {
        this.login = login;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", firstName='" + firstName + '\'' +
                ", secondName='" + secondName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull User o) {
        return login.compareTo(o.login);
    }
}