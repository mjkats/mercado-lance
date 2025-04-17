package br.com.katsilis.mercadolance.model;

import br.com.katsilis.mercadolance.enums.BidStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime bidTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;
}