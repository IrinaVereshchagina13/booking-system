package com.cinema.booking_system.repository;

import com.cinema.booking_system.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByHallIdAndRowNumberAndSeatNumber(Long hallId, Integer rowNumber, Integer seatNumber);
}