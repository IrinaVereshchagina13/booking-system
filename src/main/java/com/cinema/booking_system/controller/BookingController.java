package com.cinema.booking_system.controller;

import com.cinema.booking_system.dto.TicketBookingRequest;
import com.cinema.booking_system.dto.TicketCancelRequest;
import com.cinema.booking_system.dto.TicketDto;
import com.cinema.booking_system.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<TicketDto> bookTicket(@Valid @RequestBody TicketBookingRequest request) {
        TicketDto ticketDto = bookingService.bookTicket(request);
        return new ResponseEntity<>(ticketDto, HttpStatus.CREATED);
    }

    @PostMapping("/{ticketId}/cancel")
    public ResponseEntity<Void> cancelTicket(
            @PathVariable Long ticketId,
            @RequestBody(required = false) TicketCancelRequest request
    ) {
        TicketCancelRequest cancelRequest = (request != null) ? request : new TicketCancelRequest();
        bookingService.cancelTicket(ticketId, cancelRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{ticketId}/pay")
    public ResponseEntity<TicketDto> payTicket(@PathVariable Long ticketId) {
        TicketDto ticketDto = bookingService.payTicket(ticketId);
        return ResponseEntity.ok(ticketDto);
    }
}