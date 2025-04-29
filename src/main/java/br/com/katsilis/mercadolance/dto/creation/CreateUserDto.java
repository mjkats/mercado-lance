package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CreateUserDto {

    @NotBlank(message = "Auth0 ID is required.")
    private String auth0Id;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid email address.")
    private String email;

    @NotBlank(message = "Name is required.")
    private String name;
}