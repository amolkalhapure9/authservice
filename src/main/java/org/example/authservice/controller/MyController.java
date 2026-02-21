package org.example.authservice.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.example.authservice.dto.LoginDto;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.entity.Candidate;
import org.example.authservice.myservice.CustomUserDetails;
import org.example.authservice.myservice.EmailVerification;
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
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MyController {
    @Autowired
    SpringValidation validation;
     @Autowired
    AuthenticationManager authManager;
     @Autowired
     JwtUtil jwtToken;

     @Autowired
     EmailVerification emailVerification;

Logger logger=LoggerFactory.getLogger(MyController.class);


    @Autowired
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
    public String register(@Valid @ModelAttribute("user") RegisterDto register, BindingResult result, HttpSession session, Model model){
        if(result.hasErrors()){
            for(FieldError err: result.getFieldErrors()){
                model.addAttribute(err.getField(), err.getDefaultMessage());
                System.out.println(err.getDefaultMessage());

            }
            return "register";
        }

        else{


         Candidate candidate=myservice.findUserByEmail(register.getEmail());

         if(candidate==null){
             register.setVerification(false);
              myservice.registerUser(register);
              session.setAttribute("register",register);

              return "verify";
         }
         else{
             model.addAttribute("email", "email is already exists");
             return "register";

         }

        }



    }




@GetMapping("/send-otp")
public String verifyAccount(HttpSession session, HttpServletRequest request, Model model) {
    String email = null;
    boolean verification = false;
    RegisterDto dto = (RegisterDto) session.getAttribute("register");
//    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (dto != null) {
        email = dto.getEmail();
        verification = dto.isVerification();
        if (verification == false) {
            logger.info("User is not verified with email"+dto.getEmail());
            int otp = emailVerification.sendOtp();
            session.setAttribute("otp", otp);
            emailVerification.sendEmail(email, otp);
            model.addAttribute("successmessage", "OTP has been sent to your registered email");
            logger.info("OTP sent to "+dto.getEmail());
            return "verify";


        }
        else{
            return "login";
        }
    }
    else{
        return "welcome";
    }

}

@PostMapping("/verify")
public String verifyEmail(HttpServletRequest request, HttpSession session, Model model){
        String userOtp=request.getParameter("otp");
        try{
            int serverOtp=(int)session.getAttribute("otp");
            System.out.println(serverOtp);
            if(String.valueOf(serverOtp)!=null && userOtp.equals(String.valueOf(serverOtp))){
                RegisterDto dto=(RegisterDto) session.getAttribute("register");
                boolean b=myservice.verifyEmail(dto);

                if(b){
                    logger.info("User is verified with email"+dto.getEmail());
                    return "Authenticate";
                }
                else{
                    return "verify";
                }


            }
            else{
                model.addAttribute("message", "Please enter valid OTP");
                return "verify";
            }
        }
        catch(Exception e){
            model.addAttribute("message", "Please enter the correct OTP");
            return "verify";
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

                  logger.info("User Logged in: "+userDetails.getName());

                  return "redirect:/dashboard/"+userDetails.getName();

              }
              else {
                  System.out.println("Runninf");

                  model.addAttribute("message", "Please enter correct credentials");
                  return "login";
              }
          }


    }
    @GetMapping("/dashboard/{username}")
    public String dashboard(@PathVariable("username") String Username, HttpServletRequest req, HttpServletResponse response, Model model){
      Authentication auth= SecurityContextHolder.getContext().getAuthentication();
      if(auth!=null){
          CustomUserDetails userDetails=(CustomUserDetails) auth.getPrincipal();
          System.out.println(userDetails.getName());
          model.addAttribute("name", userDetails.getName());
      }

        return "Authenticate";
    }

    @GetMapping("/logout")
    public String logout(){
        return "welcome";

    }
}




