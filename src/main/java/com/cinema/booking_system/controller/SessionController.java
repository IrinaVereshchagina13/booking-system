package com.cinema.booking_system.controller;

import com.cinema.booking_system.dto.CreateSessionRequest;
import com.cinema.booking_system.dto.SeatDto;
import com.cinema.booking_system.dto.SessionDto;
import com.cinema.booking_system.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/{sessionId}/seats")
    public ResponseEntity<List<SeatDto>> getSeats(@PathVariable Long sessionId) {
        return ResponseEntity.ok(sessionService.getSessionSeats(sessionId));
    }

    @GetMapping
    public ResponseEntity<List<SessionDto>> getSessionsByDate(
            @RequestParam("date") LocalDate date
    ) {
        return ResponseEntity.ok(sessionService.getSessionsByDate(date));
    }

    @PostMapping
    public ResponseEntity<Void> createSession(@RequestBody CreateSessionRequest request) {
        sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}