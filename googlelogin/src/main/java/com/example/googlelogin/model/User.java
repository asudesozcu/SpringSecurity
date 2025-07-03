package com.example.googlelogin.model;


import jakarta.persistence.*;
import lombok.Data;
//import jakarta.validation.constraints.Email;


@Entity
@Table(name = "albaraka_users")
public class User {

    public User() {
    }


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "user_seq_gen")
    @SequenceGenerator(
            name = "user_seq_gen",
            sequenceName = "user_seq",
            allocationSize = 1
    )
    private Long id;
    private String name;
    // @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;
   // private String password;
    private String role ="USER";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
