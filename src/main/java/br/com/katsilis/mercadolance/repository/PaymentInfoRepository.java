package br.com.katsilis.mercadolance.repository;

import br.com.katsilis.mercadolance.model.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentInfoRepository extends JpaRepository<PaymentInfo, Long> {
}
