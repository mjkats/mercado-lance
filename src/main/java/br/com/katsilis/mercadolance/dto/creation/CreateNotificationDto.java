package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateNotificationDto {

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Message is required.")
    private String message;
}
