package br.com.katsilis.mercadolance.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String userMessage;
    private String errorMessage;
}
