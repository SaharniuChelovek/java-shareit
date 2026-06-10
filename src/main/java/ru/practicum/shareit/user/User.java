package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;
}
