package com.cinema.booking_system.mapper;

import com.cinema.booking_system.dto.SeatDto;
import com.cinema.booking_system.entity.Seat;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    List<SeatDto> toDtoList(List<Seat> seats, @Context Set<Long> occupiedSeatIds);

    @Mapping(target = "status", expression = "java(getStatus(seat.getId(), occupiedSeatIds))")
    SeatDto toDto(Seat seat, @Context Set<Long> occupiedSeatIds);

    default String getStatus(Long seatId, Set<Long> occupiedSeatIds) {
        return occupiedSeatIds.contains(seatId) ? "BOOKED" : "AVAILABLE";
    }
}