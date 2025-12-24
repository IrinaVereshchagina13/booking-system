package com.cinema.booking_system.repository;

import com.cinema.booking_system.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findByHallIdAndRowNumberAndSeatNumber(Long hallId, Integer rowNumber, Integer seatNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.hall.id = :hallId AND s.rowNumber = :rowNumber AND s.seatNumber = :seatNumber")
    Optional<Seat> findForBooking(Long hallId, Integer rowNumber, Integer seatNumber);
}