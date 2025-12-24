package com.cinema.booking_system.mapper;

import com.cinema.booking_system.dto.SessionDto;
import com.cinema.booking_system.entity.Session;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(source = "movie.title", target = "movieTitle")
    @Mapping(source = "hall.name", target = "hallName")
    SessionDto toDto(Session session);

    List<SessionDto> toDtoList(List<Session> sessions);
}