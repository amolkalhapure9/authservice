package org.example.authservice.entity;

import jakarta.persistence.*;

@Entity
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private boolean verification;

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    @Column
    private String experience;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmpssword() {
        return confirmpssword;
    }

    public void setConfirmpssword(String confirmpssword) {
        this.confirmpssword = confirmpssword;
    }

    @Transient
    private String confirmpssword;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
