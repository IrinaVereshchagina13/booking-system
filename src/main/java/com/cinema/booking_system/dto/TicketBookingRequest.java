package com.cinema.booking_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketBookingRequest {

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    @NotNull(message = "Row number is required")
    @Min(value = 1, message = "Row number must be at least 1")
    private Integer rowNumber;

    @NotNull(message = "Seat number is required")
    @Min(value = 1, message = "Seat number must be at least 1")
    private Integer seatNumber;

    @NotNull
    private Long userId;
}