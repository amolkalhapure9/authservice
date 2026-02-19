package org.example.authservice.myservice;

import org.example.authservice.dto.RegisterDto;
import org.springframework.stereotype.Service;


public interface MyService {
    public boolean registerUser(RegisterDto dto);
}
