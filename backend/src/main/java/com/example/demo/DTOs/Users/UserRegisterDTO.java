package com.example.demo.DTOs.Users;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO extends UserDTOBase{
    @NotBlank(message = "Password is mandatory")
    private String password;

    private BigDecimal preferredBudget;

}
