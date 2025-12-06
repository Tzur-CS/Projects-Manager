package com.example.demo.user.dto;

import com.example.demo.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "Username is required")
    private String username;

    private String cognitoSub;

    private User.UserRole role;

}

