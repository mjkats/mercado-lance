package br.com.katsilis.mercadolance.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NotificationResponseDto {
    private UserResponseDto user;
    private String message;
    private LocalDateTime sentAt;
    private boolean read;
}
