package org.example.authservice.controller;

import org.example.authservice.dto.LoginDto;
import org.example.authservice.dto.RegisterDto;
import org.example.authservice.entity.Candidate;
import org.example.authservice.myservice.CustomUserDetails;
import org.example.authservice.myservice.EmailVerification;
import org.example.authservice.myservice.MyService;
import org.example.authservice.myservice.MyServiceImpl;
import org.example.authservice.springvalidation.SpringValidation;
import org.example.authservice.userauthentication.JwtFilter;
import org.example.authservice.userauthentication.JwtUtil;
import org.example.authservice.securityfilter.MySecurityFilterChain;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyController.class)
@AutoConfigureMockMvc(addFilters = false)  // import your security config if needed
public class MyControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MyService service;

   @SpyBean
    SpringValidation springValidation;

    @MockBean
    JwtUtil jwtToken;

    @MockBean
    AuthenticationManager authManager;

    @MockBean
    EmailVerification emailVerification;

   @MockBean
   DaoAuthenticationProvider authprovider;


    @MockBean
    JwtFilter filter;
    @MockBean
    private MySecurityFilterChain securityFilterChain;



    // Test 1: validation fails
    @Test
    @WithMockUser
    public void returnRegisterOnValidationFails() throws Exception {

        mockMvc.perform(post("/register")
                        .param("email", "")
                        .param("password","123")
                        .param("name", "")
                        .param("experience","")
                        .param("confirmpassword","123"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    // Test 2: candidate exists
    @Test
    @WithMockUser
    public void returnRegisterOnCandidateExists() throws Exception {
        String email = "amolkalhapure@gmail.com";
        when(service.findUserByEmail(email)).thenReturn(new Candidate());

        mockMvc.perform(post("/register")
                        .param("email", email)
                        .param("password","12345")
                        .param("name", "Amol")
                        .param("experience","Begineer")
                        .param("confirmpassword","12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(service, times(1)).findUserByEmail(email);
    }

    // Test 3: new registration goes to verify page
    @Test
    @WithMockUser
    public void returnVerifyOnRegistration() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("email@gmail.com");
        dto.setPassword("12345");
        dto.setConfirmpassword("12345");
        dto.setName("Amol");
        dto.setExperience("Beginner");

        when(service.findUserByEmail(dto.getEmail())).thenReturn(null);
        doNothing().when(springValidation).validate(any(), any());

        mockMvc.perform(post("/register")
                        .flashAttr("user",dto))
                .andExpect(status().isOk())
                .andExpect(view().name("verify"));

//        verify(service, times(1)).findUserByEmail("email@gmail.com");
//        verify(service, times(1)).registerUser(any(RegisterDto.class)); // also verify registration
    }

    @Test
    @WithMockUser
    public void verifyemailtest() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("email@gmail.com");
        dto.setPassword("12345");
        dto.setConfirmpassword("12345");
        dto.setName("Amol");
        dto.setExperience("Beginner");
        boolean verification=false;
        when(emailVerification.sendOtp()).thenReturn(1234);
        doNothing().when(emailVerification).sendEmail(anyString(),anyInt());
        MockHttpSession session=new MockHttpSession();
        session.setAttribute("register",dto);

        mockMvc.perform(get("/send-otp")
                .sessionAttr("register",dto))
                .andExpect(status().isOk())
                .andExpect(view().name("verify"));

    }

    @Test
    @WithMockUser
    public void verifyEMail() throws Exception {
        RegisterDto dto = new RegisterDto();
        dto.setEmail("email@gmail.com");
        dto.setPassword("12345");
        dto.setConfirmpassword("12345");
        dto.setName("Amol");
        dto.setExperience("Beginner");



        when(service.verifyEmail(dto)).thenReturn(true);

        mockMvc.perform(post("/verify")
                        .sessionAttr("otp", 1234)
                        .sessionAttr("register", dto)
                        .param("otp", "1234"))
                .andExpect(status().isOk())
                .andExpect(view().name("Authenticate"));





    }

    @Test
    public void returnonloginParamFail() throws Exception{
        mockMvc.perform(post("/login")
                .param("email","")
                .param("email",""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

    }
    @Test
    public void returnLoginVerification() throws Exception{
       Candidate candidate=new Candidate();
       candidate.setName("Amol");
       candidate.setEmail("amol@gmail.com");
       candidate.setPassword("434454");
       candidate.setExperience("beginner");
       candidate.setVerification(true);

       CustomUserDetails customUserDetails=new CustomUserDetails(candidate);
       Authentication authentication=mock(Authentication.class);
       when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
       when(authentication.isAuthenticated()).thenReturn(true);
       when(authentication.getPrincipal()).thenReturn(customUserDetails);
       when(jwtToken.generateToken(customUserDetails)).thenReturn("Mock-JWT-Token");


        mockMvc.perform(post("/login")
                .param("email","amol@gmail.com")
                .param("password","434454"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard/Amol"))
                .andExpect(cookie().exists("JWT"))
                .andExpect(cookie().value("JWT", "Mock-JWT-Token"))
                .andExpect(cookie().httpOnly("JWT", true));



    }





}