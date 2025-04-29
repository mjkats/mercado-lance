package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CreateNotificationDto {

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Message is required.")
    private String message;
}
