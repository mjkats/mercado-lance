package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CreateProductDto {

    @NotBlank(message = "Product name is required.")
    private String name;
}