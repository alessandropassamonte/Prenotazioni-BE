package it.company.deskbooking.controller;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDetailDTO> getBookingById(@PathVariable Long id) {
        BookingDetailDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}/upcoming")
    public ResponseEntity<List<BookingDTO>> getUserUpcomingBookings(@PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getUserUpcomingBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<BookingDTO>> getBookingsForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<BookingDTO> bookings = bookingService.getBookingsForDate(date);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/date/{date}/floor/{floorId}")
    public ResponseEntity<List<BookingDTO>> getBookingsForDateAndFloor(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable Long floorId) {
        List<BookingDTO> bookings = bookingService.getBookingsForDateAndFloor(date, floorId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<BookingDTO> createBooking(
            @PathVariable Long userId,
            @RequestBody CreateBookingRequest request) {
        BookingDTO booking = bookingService.createBooking(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(
            @PathVariable Long id,
            @RequestBody UpdateBookingRequest request) {
        BookingDTO booking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) CancelBookingRequest request) {
        if (request == null) {
            request = CancelBookingRequest.builder().build();
        }
        bookingService.cancelBooking(id, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/check-in")
    public ResponseEntity<BookingDTO> checkIn(@PathVariable Long id) {
        BookingDTO booking = bookingService.checkIn(id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{id}/check-out")
    public ResponseEntity<BookingDTO> checkOut(@PathVariable Long id) {
        BookingDTO booking = bookingService.checkOut(id);
        return ResponseEntity.ok(booking);
    }
}
