package ru.practicum.ewmmain.user.dto;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@Builder
public class CreateUserDto {
    @NotBlank(message = "Name of user shouldn't be blank")
    @Size(min = 2, max = 250, message = "Name of user shouldn't be less than 2 and more than 250 characters")
    String name;
    @NotBlank(message = "Email of user shouldn't be blank")
    @Size(min = 6, max = 254, message = "Email of user shouldn't be less than 6 more than 254 characters")
    @Email(message = "Email must be in correct format.")
    String email;
}
