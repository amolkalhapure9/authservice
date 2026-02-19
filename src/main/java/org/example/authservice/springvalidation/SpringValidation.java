package org.example.authservice.springvalidation;


import org.example.authservice.dto.RegisterDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SpringValidation implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
       return RegisterDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
     RegisterDto dto=(RegisterDto) target;

     if(dto.getPassword()!=null && dto.getPassword()!=null && !dto.getPassword().equals(dto.getConfirmpassword())){
         errors.rejectValue("confirmpassword", "password.mismatch", "Password is not matching");

     }
    }
}
