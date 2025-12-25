package com.cinema.booking_system.repository;

import com.cinema.booking_system.entity.Ticket;
import com.cinema.booking_system.entity.TicketStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsBySessionIdAndSeatIdAndStatusNot(Long sessionId, Long seatId, TicketStatus status);

    List<Ticket> findAllBySessionId(Long sessionId);

    List<Ticket> findAllByStatusAndCreatedAtBefore(TicketStatus status, LocalDateTime dateTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Optional<Ticket> findByIdWithLock(Long id);
}