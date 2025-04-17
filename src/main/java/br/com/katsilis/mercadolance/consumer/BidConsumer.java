package br.com.katsilis.mercadolance.consumer;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class BidConsumer {

    //TODO: Identificar mensagem enviada e criar um DTO para a mesma
    @SqsListener("mercado-lance-queue")
    public void receiveMessage(String messageBody) {

    }
}
