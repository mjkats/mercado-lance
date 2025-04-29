package br.com.katsilis.mercadolance.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserResponseDto {
    private String email;
    private String name;
}
