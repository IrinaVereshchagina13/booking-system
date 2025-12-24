package com.cinema.booking_system.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateSessionRequest {
    private Long movieId;
    private Long hallId;
    private LocalDateTime startTime;
    private BigDecimal price;
}