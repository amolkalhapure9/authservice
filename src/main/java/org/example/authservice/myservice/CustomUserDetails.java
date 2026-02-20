package org.example.authservice.myservice;

import org.example.authservice.entity.Candidate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private String email;
    private String password;
    private String name;

    private boolean verification;

    public CustomUserDetails(Candidate candidate) {
//        this.id = candidate.getId();
        this.email = candidate.getEmail();
        this.password = candidate.getPassword();
        this.name = candidate.getName();
        this.verification=candidate.isVerification();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public boolean isVerification() {
        return verification;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getName(){
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
