package com.cinema.booking_system;

import com.cinema.booking_system.dto.TicketBookingRequest;
import com.cinema.booking_system.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    private BookingService bookingService;

    private Long sessionId;
    private final int ROW = 1;
    private final int SEAT = 1;

    @BeforeEach
    void setUp() {
        clearDatabase();
        var session = createSessionWithSeat(ROW, SEAT);
        sessionId = session.getId();
    }

    @Test
    @DisplayName("Race Condition: 10 users try to buy 1 ticket")
    void shouldSellOnlyOneTicket_WhenConcurrencyHigh() throws InterruptedException {
        int threadCount = 10;
        var executor = Executors.newFixedThreadPool(threadCount);
        var latch = new CountDownLatch(1);

        List<CompletableFuture<Boolean>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        latch.await();
                        return tryToBookTicket(100L + i);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return false;
                    }
                }, executor))
                .toList();

        latch.countDown();

        long successCount = futures.stream()
                .map(CompletableFuture::join)
                .filter(isSuccess -> isSuccess)
                .count();

        assertEquals(1, ticketRepository.count());
        assertEquals(1, successCount);
    }

    private boolean tryToBookTicket(Long userId) {
        try {
            var request = new TicketBookingRequest();
            request.setSessionId(sessionId);
            request.setRowNumber(ROW);
            request.setSeatNumber(SEAT);
            request.setUserId(userId);

            bookingService.bookTicket(request);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}