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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final TicketRepository ticketRepository;
    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final TicketMapper ticketMapper;
    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_KEY_TEMPLATE = "ticket:session:%d:seat:%d";

    @Transactional
    public TicketDto bookTicket(TicketBookingRequest request) {
        Session session = findSession(request.getSessionId());
        Long seatId = findSeatIdForRedis(session, request);

        String redisKey = buildRedisKey(session.getId(), seatId);

        acquireRedisLock(redisKey, request.getUserId());

        try {
            Seat seat = findAndLockSeat(session, request);

            validateSeatAvailable(session.getId(), seat.getId());

            Ticket ticket = saveTicket(session, seat, request.getUserId());

            return ticketMapper.toDto(ticket);

        } catch (Exception e) {
            redisTemplate.delete(redisKey);
            throw e;
        }
    }

    @Transactional
    public void cancelTicket(Long ticketId, TicketCancelRequest request) {
        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with id: " + ticketId));

        if (ticket.getStatus() == TicketStatus.PAID) {
            throw new IllegalStateException("Cannot cancel paid ticket");
        }

        if (ticket.getStatus() == TicketStatus.CANCELED) {
            return;
        }

        ticket.setStatus(TicketStatus.CANCELED);
        ticketRepository.save(ticket);

        String redisKey = String.format(LOCK_KEY_TEMPLATE, ticket.getSession().getId(), ticket.getSeat().getId());
        redisTemplate.delete(redisKey);

        System.out.println("Ticket " + ticketId + " canceled. Reason: " + request.getReason());
    }

    @Transactional
    public TicketDto payTicket(Long ticketId){
        Ticket ticket = ticketRepository.findByIdWithLock(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (ticket.getStatus() == TicketStatus.CANCELED){
            throw new IllegalStateException("Ticket is canceled and cannot be paid");
        }

        if (ticket.getStatus() == TicketStatus.PAID){
            throw new IllegalStateException("Ticket is already paid");
        }

        ticket.setStatus(TicketStatus.PAID);
        Ticket savedTicket = ticketRepository.save(ticket);

        return ticketMapper.toDto(savedTicket);
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

    private Session findSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));
    }

    private Long findSeatIdForRedis(Session session, TicketBookingRequest request) {
        return seatRepository.findByHallIdAndRowNumberAndSeatNumber(
                session.getHall().getId(),
                request.getRowNumber(),
                request.getSeatNumber()
        ).map(Seat::getId).orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
    }

    private String buildRedisKey(Long sessionId, Long seatId) {
        return String.format(LOCK_KEY_TEMPLATE, sessionId, seatId);
    }

    private void acquireRedisLock(String key, Long userId) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(
                key,
                String.valueOf(userId),
                Duration.ofMinutes(5)
        );
        if (Boolean.FALSE.equals(success)) {
            throw new SeatAlreadyBookedException("Seat is temporarily reserved by another user");
        }
    }

    private Seat findAndLockSeat(Session session, TicketBookingRequest request) {
        return seatRepository.findForBooking(
                session.getHall().getId(),
                request.getRowNumber(),
                request.getSeatNumber()
        ).orElseThrow(() -> new ResourceNotFoundException("Seat not found"));
    }

    private Ticket saveTicket(Session session, Seat seat, Long userId) {
        Ticket ticket = Ticket.builder()
                .userId(userId)
                .session(session)
                .seat(seat)
                .status(TicketStatus.BOOKED)
                .build();
        return ticketRepository.save(ticket);
    }
}