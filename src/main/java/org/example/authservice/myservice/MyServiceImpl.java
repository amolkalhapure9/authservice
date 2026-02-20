package org.example.authservice.myservice;

import org.example.authservice.dto.RegisterDto;
import org.example.authservice.entity.Candidate;
import org.example.authservice.myrepository.MyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Service
public class MyServiceImpl implements MyService, UserDetailsService {
    @Autowired
    MyRepository repository;

    @Autowired
    BCryptPasswordEncoder encoder;

    public boolean registerUser(RegisterDto dto){
        Candidate candidate=new Candidate();
        candidate.setName(dto.getName());
        candidate.setEmail(dto.getEmail());
        candidate.setExperience(dto.getExperience());
        candidate.setVerification(dto.isVerification());

        String password=dto.getPassword();
        String encoded=encoder.encode(password);
        candidate.setPassword(encoded);

        Candidate isUserExists=findUserByEmail(dto.getEmail());
        if(isUserExists==null){
            Candidate cand=repository.save(candidate);

            if(cand.getEmail()!=null){
                return true;
            }
            else{
                return false;
            }

        }

        return false;


    }

    public boolean verifyEmail(RegisterDto dto){
        Candidate candidate=new Candidate();
        candidate.setEmail(dto.getEmail());

        int rowsUpdates=repository.verifyUserByEmail(candidate.getEmail());
        if(rowsUpdates>0){
            return true;
        }
        else{
            return false;
        }

    }

    public Candidate findUserByEmail(String email){
        Candidate candidate=repository.findByEmail(email);
        return candidate;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Candidate candidate=repository.findByEmail(username);
        return new CustomUserDetails(candidate);
    }
}
