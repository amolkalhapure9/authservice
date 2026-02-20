package org.example.authservice.myservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class EmailVerification {
    @Autowired
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
   String sender;


    public int sendOtp(){
        SecureRandom random = new SecureRandom();
        int otp = 1000 + random.nextInt(9000);
        return otp;
    }

    public void sendEmail(String email, int otp){
        SimpleMailMessage message=new SimpleMailMessage();
        message.setText(email);
        message.setFrom(sender);
        message.setTo(email);
        message.setSubject("OTP verification");
        message.setText("Hi OTP to verify your email :"+otp);

        mailSender.send(message);

    }

}
