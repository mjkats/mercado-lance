package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.enums.AuctionStatus;
import br.com.katsilis.mercadolance.model.Auction;
import br.com.katsilis.mercadolance.service.AuctionService;
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
    public ResponseEntity<List<Auction>> getAll() {
        return ResponseEntity.ok(auctionService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Auction>> getAuctions(@RequestParam(required = false) AuctionStatus status,
                                                     @RequestParam(required = false) String productName,
                                                     @PageableDefault(sort = "title", size = 20) Pageable pageable) {
        return ResponseEntity.ok(auctionService.getAuctions(status, productName, pageable));
    }

    @GetMapping("/status")
    public ResponseEntity<List<Auction>> getByStatus(@RequestParam AuctionStatus status) {
        return ResponseEntity.ok(auctionService.findByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auctionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Auction> create(@RequestBody Auction auction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auctionService.save(auction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Auction> update(@PathVariable Long id, @RequestBody Auction auction) {
        Auction updatedAuction = auctionService.update(id, auction);

        return !auction.equals(updatedAuction)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        auctionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}