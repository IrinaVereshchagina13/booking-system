package com.cinema.booking_system.dto;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SessionDto implements Serializable {
    private Long id;
    private String movieTitle;
    private String hallName;
    private LocalDateTime startTime;
    private BigDecimal price;
}