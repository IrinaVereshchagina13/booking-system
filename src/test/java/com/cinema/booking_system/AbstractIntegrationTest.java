package com.cinema.booking_system;

import com.cinema.booking_system.entity.*;
import com.cinema.booking_system.repository.CinemaRepository;
import com.cinema.booking_system.repository.HallRepository;
import com.cinema.booking_system.repository.MovieRepository;
import com.cinema.booking_system.repository.SeatRepository;
import com.cinema.booking_system.repository.SessionRepository;
import com.cinema.booking_system.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {
    @Autowired protected CinemaRepository cinemaRepository;
    @Autowired protected HallRepository hallRepository;
    @Autowired protected SeatRepository seatRepository;
    @Autowired protected MovieRepository movieRepository;
    @Autowired protected SessionRepository sessionRepository;
    @Autowired protected TicketRepository ticketRepository;

    protected Session createSessionWithSeat(int row, int seatNum) {
        var cinema = cinemaRepository.save(Cinema.builder().name("Kino").city("Msk").build());
        var hall = hallRepository.save(new Hall(null, "Hall 1", cinema));
        var movie = movieRepository.save(new Movie(null, "Movie", "Desc", 120));

        seatRepository.save(new Seat(null, hall, row, seatNum));

        return sessionRepository.save(Session.builder()
                .hall(hall)
                .movie(movie)
                .startTime(LocalDateTime.now().plusHours(1))
                .price(BigDecimal.TEN)
                .build());
    }

    protected void clearDatabase() {
        ticketRepository.deleteAll();
        sessionRepository.deleteAll();
        seatRepository.deleteAll();
        hallRepository.deleteAll();
        cinemaRepository.deleteAll();
        movieRepository.deleteAll();
    }
}