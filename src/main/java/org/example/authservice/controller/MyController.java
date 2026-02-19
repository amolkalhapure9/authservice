package org.example.authservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.example.authservice.dto.LoginDto;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.entity.Candidate;
import org.example.authservice.myservice.CustomUserDetails;
import org.example.authservice.myservice.MyService;
import org.example.authservice.securityfilter.MySecurityFilterChain;
import org.example.authservice.springvalidation.SpringValidation;
import org.example.authservice.userauthentication.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MyController {
    @Autowired
    SpringValidation validation;
     @Autowired
    AuthenticationManager authManager;
     @Autowired
     JwtUtil jwtToken;




    @Autowired
    @Qualifier("myServiceImpl")
    MyService myservice;

    @InitBinder("user")
    protected void initBindeer(WebDataBinder binder){
        binder.addValidators(validation);

    }

    @GetMapping("/welcome")
    public String MyWelcomePage(){
    return "welcome";

    }
    @GetMapping("/registerpage")
    public String openRegisterpage(Model model){
        model.addAttribute("user", new RegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterDto register, BindingResult result, Model model){
        if(result.hasErrors()){
            for(FieldError err: result.getFieldErrors()){
                model.addAttribute(err.getField(), err.getDefaultMessage());
                System.out.println(err.getDefaultMessage());

            }
            return "register";
        }

        else{

         boolean isRegistered=myservice.registerUser(register);
      System.out.println(isRegistered);
         if(isRegistered){
             return "welcome";
         }
         else{
             model.addAttribute("email", "email is already exists");
             return "register";

         }



        }






    }

    @GetMapping("/loginpage")
    public String openLoginPage(Model model){
        model.addAttribute("authuser", new LoginDto());
        return "login";
    }
    @PostMapping("login")
    public String loginUser(@Valid @ModelAttribute("authuser") LoginDto dto, BindingResult result, Model model, HttpServletRequest req, HttpServletResponse response) {
          if(result.hasErrors()){
              result.getFieldErrors().forEach(fieldError ->
                      model.addAttribute(fieldError.getField(),fieldError.getDefaultMessage()));

              return "login";
          }
          else{
              String username=dto.getEmail();
              String passsword=dto.getPassword();
              UsernamePasswordAuthenticationToken token=new UsernamePasswordAuthenticationToken(username, passsword);
              Authentication authentication=authManager.authenticate(token);

              if(authentication.isAuthenticated()){
                  CustomUserDetails userDetails=(CustomUserDetails) authentication.getPrincipal();
                  String Jwts= jwtToken.generateToken(userDetails);
                  Cookie cookie =new Cookie("JWT", Jwts);
                  cookie.setHttpOnly(true);
                  cookie.setSecure(false);
                  cookie.setMaxAge(30*60);
                  cookie.setPath("/");

                  response.addCookie(cookie);



                  return "welcome";

              }


              return "login";
          }


    }
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest req, HttpServletResponse response, Model model){
      Authentication auth= SecurityContextHolder.getContext().getAuthentication();
      if(auth!=null){
          CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
          System.out.println(userDetails.getName());
          model.addAttribute("name", userDetails.getName());
      }

        return "Authenticate";
    }
}




