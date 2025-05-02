package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.creation.CreateAuctionDto;
import br.com.katsilis.mercadolance.dto.response.AuctionBidResponseDto;
import br.com.katsilis.mercadolance.dto.response.AuctionResponseDto;
import br.com.katsilis.mercadolance.dto.update.UpdateAuctionDto;
import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.service.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping
    public ResponseEntity<List<AuctionResponseDto>> getAll() {
        return ResponseEntity.ok(auctionService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AuctionResponseDto>> getAuctions(@RequestParam(required = false) AuctionStatus status,
                                                                @RequestParam(required = false) String productName,
                                                                @PageableDefault(sort = "title", size = 20) Pageable pageable) {
        return ResponseEntity.ok(auctionService.getAuctions(status, productName, pageable));
    }

    @GetMapping("/status")
    public ResponseEntity<List<AuctionBidResponseDto>> getByStatus(@RequestParam AuctionStatus status) {
        return ResponseEntity.ok(auctionService.findByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionBidResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody @Valid CreateAuctionDto auction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.create(auction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateAuctionDto auction) {
        auctionService.update(id, auction);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auctionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}