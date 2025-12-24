package com.cinema.booking_system.mapper;

import com.cinema.booking_system.dto.TicketDto;
import com.cinema.booking_system.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    @Mapping(source = "session.movie.title", target = "movieTitle")
    @Mapping(source = "session.hall.cinema.name", target = "cinemaName")
    @Mapping(source = "session.hall.name", target = "hallName")
    @Mapping(source = "session.startTime", target = "startTime")
    @Mapping(source = "seat.rowNumber", target = "rowNumber")
    @Mapping(source = "seat.seatNumber", target = "seatNumber")
    @Mapping(source = "session.price", target = "price")
    TicketDto toDto(Ticket ticket);
}