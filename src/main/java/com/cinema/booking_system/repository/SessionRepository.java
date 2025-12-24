package com.cinema.booking_system.repository;

import com.cinema.booking_system.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("SELECT s FROM Session s WHERE s.startTime BETWEEN :start AND :end")
    List<Session> findSessionsByDate(LocalDateTime start, LocalDateTime end);

    List<Session> findAllByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}