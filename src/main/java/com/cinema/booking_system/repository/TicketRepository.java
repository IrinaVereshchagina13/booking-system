package com.cinema.booking_system.repository;

import com.cinema.booking_system.entity.Ticket;
import com.cinema.booking_system.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsBySessionIdAndSeatIdAndStatusNot(Long sessionId, Long seatId, TicketStatus status);
}