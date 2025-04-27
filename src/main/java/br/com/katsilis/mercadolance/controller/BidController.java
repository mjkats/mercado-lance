package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.BidDto;
import br.com.katsilis.mercadolance.enums.BidStatus;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public ResponseEntity<Bid> create(@Valid @RequestBody BidDto bid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bidService.create(bid));
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

    @GetMapping("/bid-updates/{auctionId}")
    public SseEmitter streamBidUpdates(@PathVariable Long auctionId) {
        SseEmitter emitter = new SseEmitter();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
            executor.submit(() -> {
                try {
                    while (true) {
                        Bid updatedBid = bidService.getLatestAuctionBid(auctionId);
                        emitter.send(updatedBid);
                        Thread.sleep(1000);
                    }
                } catch (IOException | InterruptedException e) {
                    emitter.completeWithError(e);
                    throw e;
                }
            });
        } catch (Exception e) {
            emitter.completeWithError(e);
        } finally {
            executor.close();
            executor.shutdown();
        }

        return emitter;
    }
}
