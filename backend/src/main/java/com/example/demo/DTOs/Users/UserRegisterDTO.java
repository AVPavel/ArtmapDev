package com.example.demo.DTOs.Users;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO extends UserDTOBase{
    @NotBlank(message = "Password is mandatory")
    private String password;

}
