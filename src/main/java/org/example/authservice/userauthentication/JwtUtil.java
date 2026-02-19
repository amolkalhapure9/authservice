package org.example.authservice.userauthentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.authservice.myservice.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Time;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

import javax.sql.DataSource;

@Component
public class JwtUtil {
    private String secret_key="vejhewvjewrh87545vewwevwrtw4984343vttt455435";
    private long expirationtime=1000*60*30;

  public Key getKey(){
      return Keys.hmacShaKeyFor(secret_key.getBytes());
  }

  public String generateToken(CustomUserDetails userDetails){

      return Jwts.builder()
              .setSubject(userDetails.getUsername())
              .claim("name",userDetails.getName())
              .setIssuedAt(new Date())
              .setExpiration(new Date(System.currentTimeMillis()+expirationtime))
              .signWith(getKey(), SignatureAlgorithm.HS256)
              .compact();





  }

}
