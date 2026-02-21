package org.example.authservice.myservice;

import org.example.authservice.dto.RegisterDto;
import org.example.authservice.entity.Candidate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


public interface MyService {
    public boolean registerUser(RegisterDto dto);
    public Candidate findUserByEmail(String email);
    public boolean verifyEmail(RegisterDto dto);
    public UserDetails loadUserByUsername(String username);

}
