package com.cinema.booking_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatDto {
    private Long id;
    private Integer rowNumber;
    private Integer seatNumber;
    private String status;
}