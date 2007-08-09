package org.springframework.webflow.samples.booking.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.hibernate.validator.Length;
//import org.hibernate.validator.NotNull;
//import org.hibernate.validator.Pattern;

@Entity
@Table(name = "Customer")
public class User implements Serializable {
    private String username;
    private String password;
    private String name;

    public User(String name, String password, String username) {
	this.name = name;
	this.password = password;
	this.username = username;
    }

    public User() {
    }

    // @NotNull
    // @Length(max = 100)
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    // @NotNull
    // @Length(min = 5, max = 15)
    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

    @Id
    // @Length(min = 5, max = 15)
    // @Pattern(regex = "^\\w*$", message = "not a valid username")
    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    @Override
    public String toString() {
	return "User(" + username + ")";
    }
}
