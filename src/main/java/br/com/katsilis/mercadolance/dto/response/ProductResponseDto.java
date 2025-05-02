package br.com.katsilis.mercadolance.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductResponseDto {
    private Long id;
    private String name;
}
