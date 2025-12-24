package com.cinema.booking_system.service;

import com.cinema.booking_system.dto.SeatDto;
import com.cinema.booking_system.entity.Seat;
import com.cinema.booking_system.entity.Session;
import com.cinema.booking_system.entity.Ticket;
import com.cinema.booking_system.entity.TicketStatus;
import com.cinema.booking_system.exception.ResourceNotFoundException;
import com.cinema.booking_system.mapper.SeatMapper;
import com.cinema.booking_system.repository.SeatRepository;
import com.cinema.booking_system.repository.SessionRepository;
import com.cinema.booking_system.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final SeatMapper seatMapper;

    @Transactional(readOnly = true)
    public List<SeatDto> getSessionSeats(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        List<Seat> allSeats = seatRepository.findAllByHallId(session.getHall().getId());
        List<Ticket> tickets = ticketRepository.findAllBySessionId(sessionId);

        Set<Long> occupiedSeatIds = tickets.stream()
                .filter(ticket -> ticket.getStatus() != TicketStatus.CANCELED)
                .map(ticket -> ticket.getSeat().getId())
                .collect(Collectors.toSet());

        return seatMapper.toDtoList(allSeats, occupiedSeatIds);
    }
}