package com.cinema.booking_system.repository;

import com.cinema.booking_system.entity.Ticket;
import com.cinema.booking_system.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsBySessionIdAndSeatIdAndStatusNot(Long sessionId, Long seatId, TicketStatus status);

    List<Ticket> findAllBySessionId(Long sessionId);

    List<Ticket> findAllByStatusAndCreatedAtBefore(TicketStatus status, LocalDateTime dateTime);
}