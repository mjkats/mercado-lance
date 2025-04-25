package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.enums.BidStatus;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @GetMapping
    public ResponseEntity<List<Bid>> getAll() {
        return ResponseEntity.ok(bidService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Bid>> getBids(@RequestParam(required = false) BidStatus status,
                                             @RequestParam(required = false) Long auctionId,
                                             @RequestParam(required = false) Long userId,
                                             @PageableDefault(sort = "bidTime", direction = Sort.Direction.DESC, size = 20) Pageable pageable) {
        return ResponseEntity.ok(bidService.getBids(auctionId, userId, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bidService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Bid> create(@RequestBody Bid bid) {
        return ResponseEntity.ok(bidService.save(bid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bid> update(@PathVariable Long id, @RequestBody Bid bid) {
        Bid updatedBid = bidService.update(id, bid);

        return !bid.equals(updatedBid)
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bidService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
