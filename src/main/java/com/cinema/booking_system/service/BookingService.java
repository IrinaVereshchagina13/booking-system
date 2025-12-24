package com.cinema.booking_system.service;

import com.cinema.booking_system.dto.TicketBookingRequest;
import com.cinema.booking_system.dto.TicketCancelRequest;
import com.cinema.booking_system.dto.TicketDto;
import com.cinema.booking_system.entity.*;
import com.cinema.booking_system.exception.ResourceNotFoundException;
import com.cinema.booking_system.exception.SeatAlreadyBookedException;
import com.cinema.booking_system.mapper.TicketMapper;
import com.cinema.booking_system.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TicketRepository ticketRepository;
    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketDto bookTicket(TicketBookingRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + request.getSessionId()));

        Seat seat = seatRepository.findByHallIdAndRowNumberAndSeatNumber(
                session.getHall().getId(),
                request.getRowNumber(),
                request.getSeatNumber()
        ).orElseThrow(() -> new ResourceNotFoundException("Seat not found at row " + request.getRowNumber() + " seat " + request.getSeatNumber()));

        validateSeatAvailable(session.getId(), seat.getId());

        Ticket ticket = Ticket.builder()
                .userId(request.getUserId())
                .session(session)
                .seat(seat)
                .status(TicketStatus.BOOKED)
                .build();

        Ticket savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toDto(savedTicket);
    }

    @Transactional
    public void cancelTicket(Long ticketId, TicketCancelRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (ticket.getStatus() == TicketStatus.CANCELED) {
            return;
        }

        ticket.setStatus(TicketStatus.CANCELED);
        ticketRepository.save(ticket);

        System.out.println("Ticket " + ticketId + " canceled. Reason: " + request.getReason());
    }

    private void validateSeatAvailable(Long sessionId, Long seatId) {
        boolean isTaken = ticketRepository.existsBySessionIdAndSeatIdAndStatusNot(
                sessionId,
                seatId,
                TicketStatus.CANCELED
        );

        if (isTaken) {
            throw new SeatAlreadyBookedException("Seat is already booked");
        }
    }
}