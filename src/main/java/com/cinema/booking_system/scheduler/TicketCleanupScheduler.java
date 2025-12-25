package com.cinema.booking_system.scheduler;

import com.cinema.booking_system.dto.TicketCancelRequest;
import com.cinema.booking_system.entity.Ticket;
import com.cinema.booking_system.entity.TicketStatus;
import com.cinema.booking_system.repository.TicketRepository;
import com.cinema.booking_system.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;


@Component
@RequiredArgsConstructor
public class TicketCleanupScheduler {
    private final TicketRepository ticketRepository;
    private final BookingService bookingService;

    @Value("${app.scheduling.booking-expiration-minutes}")
    private int expirationMinutes;

    @Scheduled(cron = "${app.scheduling.ticket-cleanup-cron}")
    @Transactional
    public void cancelExpiredBookings(){
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(expirationMinutes);

        List<Ticket> expiredTickets = ticketRepository.findAllByStatusAndCreatedAtBefore(
                TicketStatus.BOOKED,
                cutoffTime
        );

        if (expiredTickets.isEmpty()){
            return;
        }

        System.out.println("Found " + expiredTickets.size() + " expired tickets. Cleaning up...");

        TicketCancelRequest autoCancelRequest = new TicketCancelRequest();
        autoCancelRequest.setReason("Auto-cancellation: Payment timeout");

        for (Ticket ticket : expiredTickets) {
            try {
                bookingService.cancelTicket(ticket.getId(), autoCancelRequest);
                System.out.println("Auto-canceled ticket ID: " + ticket.getId());
            } catch (Exception e) {
                System.err.println("Failed to cancel ticket " + ticket.getId() + ": " + e.getMessage());
            }
        }
    }
}
