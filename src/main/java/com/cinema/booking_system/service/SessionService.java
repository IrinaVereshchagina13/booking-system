package com.cinema.booking_system.service;

import com.cinema.booking_system.dto.CreateSessionRequest;
import com.cinema.booking_system.dto.SeatDto;
import com.cinema.booking_system.dto.SessionDto;
import com.cinema.booking_system.entity.Hall;
import com.cinema.booking_system.entity.Movie;
import com.cinema.booking_system.entity.Seat;
import com.cinema.booking_system.entity.Session;
import com.cinema.booking_system.entity.Ticket;
import com.cinema.booking_system.entity.TicketStatus;
import com.cinema.booking_system.exception.ResourceNotFoundException;
import com.cinema.booking_system.mapper.SeatMapper;
import com.cinema.booking_system.mapper.SessionMapper;
import com.cinema.booking_system.repository.HallRepository;
import com.cinema.booking_system.repository.MovieRepository;
import com.cinema.booking_system.repository.SeatRepository;
import com.cinema.booking_system.repository.SessionRepository;
import com.cinema.booking_system.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SeatRepository seatRepository;
    private final TicketRepository ticketRepository;
    private final MovieRepository movieRepository;
    private final HallRepository hallRepository;

    private final SeatMapper seatMapper;
    private final SessionMapper sessionMapper;

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

    @Transactional(readOnly = true)
    @Cacheable(value = "sessions", key = "#date")
    public List<SessionDto> getSessionsByDate(LocalDate date) {
        simulateSlowDatabase();

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Session> sessions = sessionRepository.findAllByStartTimeBetween(startOfDay, endOfDay);
        return sessionMapper.toDtoList(sessions);
    }

    @Transactional
    @CacheEvict(value = "sessions", key = "#request.startTime.toLocalDate()")
    public void createSession(CreateSessionRequest request) {
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

        Hall hall = hallRepository.findById(request.getHallId())
                .orElseThrow(() -> new ResourceNotFoundException("Hall not found"));

        Session session = Session.builder()
                .movie(movie)
                .hall(hall)
                .startTime(request.getStartTime())
                .price(request.getPrice())
                .build();

        sessionRepository.save(session);
    }

    private void simulateSlowDatabase() {
        try {
            System.out.println("Going to Database... (Cache Miss)");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}