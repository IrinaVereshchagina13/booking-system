package com.cinema.booking_system.dto;

import com.cinema.booking_system.entity.TicketStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TicketDto {
    private Long id;
    private String movieTitle;
    private String cinemaName;
    private String hallName;
    private LocalDateTime startTime;
    private Integer rowNumber;
    private Integer seatNumber;
    private BigDecimal price;
    private TicketStatus status;
}