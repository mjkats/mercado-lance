package br.com.katsilis.mercadolance.controller;

import br.com.katsilis.mercadolance.dto.creation.CreateBidDto;
import br.com.katsilis.mercadolance.dto.response.BidResponseDto;
import br.com.katsilis.mercadolance.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/bids")
@RequiredArgsConstructor
@Slf4j
public class BidController {

    private final BidService bidService;

    @GetMapping
    public ResponseEntity<List<BidResponseDto>> getAll() {
        return ResponseEntity.ok(bidService.findAll());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BidResponseDto>> getBids(@RequestParam(required = false) Long auctionId,
                                             @RequestParam(required = false) Long userId,
                                             @PageableDefault(sort = "bidTime", direction = Sort.Direction.DESC, size = 20) Pageable pageable) {
        return ResponseEntity.ok(bidService.getBids(auctionId, userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BidResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bidService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateBidDto bid) {
        bidService.create(bid);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bidService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/bid-updates/{auctionId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamBidUpdates(@PathVariable Long auctionId) {
        SseEmitter emitter = new SseEmitter(0L);
        bidService.handleBidUpdates(emitter, auctionId);
        return emitter;
    }
}
