package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.enums.BidStatus;
import br.com.katsilis.mercadolance.model.Bid;
import br.com.katsilis.mercadolance.repository.BidRepository;
import br.com.katsilis.mercadolance.service.BidService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;

    @Override
    public List<Bid> findAll() {
        return bidRepository.findAll();
    }

    @Override
    public Page<Bid> getBids(Long auctionId, Long userId, BidStatus status, Pageable pageable) {

        if (auctionId != null && userId != null && status != null)
            return bidRepository.findByAuction_IdAndUser_IdAndBidStatus(auctionId, userId, status, pageable);

        if (auctionId != null && userId != null)
            return bidRepository.findByAuction_IdAndUser_Id(auctionId, userId, pageable);

        if (auctionId != null && status != null)
            return bidRepository.findByAuction_IdAndBidStatus(auctionId, status, pageable);

        if (userId != null && status != null)
            return bidRepository.findByUser_IdAndBidStatus(userId, status, pageable);

        if (userId != null)
            return bidRepository.findByUser_Id(userId, pageable);

        if (auctionId != null)
            return bidRepository.findByAuction_Id(auctionId, pageable);

        if (status != null)
            return bidRepository.findByBidStatus(status, pageable);

        return bidRepository.findAll(pageable);
    }

    @Override
    public Bid findById(Long id) {
        return bidRepository.findById(id).orElseThrow(() ->
            new EntityNotFoundException("Bid with id " + id + " not found"));
    }

    @Override
    public Bid save(Bid bid) {
        return bidRepository.save(bid);
    }

    @Override
    public void delete(Long id) {
        bidRepository.deleteById(id);
    }

    @Override
    public Bid update(Long id, Bid bid) {
        Bid existing = findById(id);
        existing.setBidTime(bid.getBidTime());
        existing.setAmount(bid.getAmount());
        existing.setStatus(bid.getStatus());

        return bidRepository.save(existing);
    }
}
