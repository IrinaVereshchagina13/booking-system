package com.cinema.booking_system.controller;

import com.cinema.booking_system.dto.SeatDto;
import com.cinema.booking_system.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}