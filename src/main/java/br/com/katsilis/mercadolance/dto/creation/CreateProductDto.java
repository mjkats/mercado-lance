package br.com.katsilis.mercadolance.dto.creation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProductDto {

    @NotBlank(message = "Product name is required.")
    private String name;
}