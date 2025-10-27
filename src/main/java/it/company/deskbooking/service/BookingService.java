package it.company.deskbooking.service;

import it.company.deskbooking.dto.*;
import it.company.deskbooking.exception.BookingException;
import it.company.deskbooking.exception.ResourceNotFoundException;
import it.company.deskbooking.model.Booking;
import it.company.deskbooking.model.Desk;
import it.company.deskbooking.model.User;
import it.company.deskbooking.repository.BookingRepository;
import it.company.deskbooking.repository.DeskRepository;
import it.company.deskbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DeskRepository deskRepository;
    private final UserRepository userRepository;

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BookingDetailDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata con id: " + id));
        return convertToDetailDTO(booking);
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getUserUpcomingBookings(Long userId) {
        return bookingRepository.findUpcomingBookingsByUser(userId, LocalDate.now()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getUserUpcomingListBookings(Long userId) {
        return bookingRepository.findUpcomingListBookingsByUser(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsForDate(LocalDate date) {
        return bookingRepository.findActiveBookingsForDate(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BookingDTO> getBookingsForDateAndFloor(LocalDate date, Long floorId) {
        return bookingRepository.findActiveBookingsForDateAndFloor(date, floorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingDTO createBooking(Long userId, CreateBookingRequest request) {
        log.info("Creazione prenotazione per utente {} per la data {}", userId, request.getBookingDate());

        // Validazioni
        validateBookingRequest(userId, request);

        // Verifica disponibilità desk
        checkDeskAvailability(request.getDeskId(), request.getBookingDate());

        // Verifica che l'utente non abbia già una prenotazione per quella data
        checkUserBookingConflict(userId, request.getBookingDate());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        Desk desk = deskRepository.findById(request.getDeskId())
                .orElseThrow(() -> new ResourceNotFoundException("Postazione non trovata"));

        Booking booking = Booking.builder()
                .user(user)
                .desk(desk)
                .bookingDate(request.getBookingDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .type(request.getType())
                .notes(request.getNotes())
                .status(Booking.BookingStatus.ACTIVE)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Prenotazione creata con successo: {}", savedBooking.getId());

        return convertToDTO(savedBooking);
    }

    @Transactional
    public BookingDTO updateBooking(Long bookingId, UpdateBookingRequest request) {
        log.info("Aggiornamento prenotazione: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata"));

        if (!booking.isActive()) {
            throw new BookingException("Non è possibile modificare una prenotazione non attiva");
        }

        if (request.getBookingDate() != null) {
            if (request.getBookingDate().isBefore(LocalDate.now())) {
                throw new BookingException("Non è possibile prenotare per date passate");
            }
            checkDeskAvailability(booking.getDesk().getId(), request.getBookingDate());
            booking.setBookingDate(request.getBookingDate());
        }

        if (request.getStartTime() != null) {
            booking.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            booking.setEndTime(request.getEndTime());
        }
        if (request.getNotes() != null) {
            booking.setNotes(request.getNotes());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Prenotazione aggiornata con successo: {}", updatedBooking.getId());

        return convertToDTO(updatedBooking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, CancelBookingRequest request) {
        log.info("Cancellazione prenotazione: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata"));

        if (!booking.isActive()) {
            throw new BookingException("La prenotazione è già stata cancellata o completata");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(request.getCancellationReason());

        bookingRepository.save(booking);
        log.info("Prenotazione cancellata con successo: {}", bookingId);
    }

    @Transactional
    public BookingDTO checkIn(Long bookingId) {
        log.info("Check-in prenotazione: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata"));

        if (!booking.canCheckIn()) {
            throw new BookingException("Non è possibile effettuare il check-in per questa prenotazione");
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        booking.setCheckedInAt(LocalDateTime.now());

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Check-in effettuato con successo per la prenotazione: {}", bookingId);

        return convertToDTO(updatedBooking);
    }

    @Transactional
    public BookingDTO checkOut(Long bookingId) {
        log.info("Check-out prenotazione: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione non trovata"));

        if (!booking.canCheckOut()) {
            throw new BookingException("Non è possibile effettuare il check-out per questa prenotazione");
        }

        booking.setStatus(Booking.BookingStatus.CHECKED_OUT);
        booking.setCheckedOutAt(LocalDateTime.now());

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Check-out effettuato con successo per la prenotazione: {}", bookingId);

        return convertToDTO(updatedBooking);
    }

    private void validateBookingRequest(Long userId, CreateBookingRequest request) {
        if (request.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException("Non è possibile prenotare per date passate");
        }

        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getEndTime().isBefore(request.getStartTime())) {
                throw new BookingException("L'orario di fine deve essere successivo all'orario di inizio");
            }
        }
    }

    private void checkDeskAvailability(Long deskId, LocalDate date) {
        bookingRepository.findActiveBookingByDeskAndDate(deskId, date)
                .ifPresent(b -> {
                    throw new BookingException("La postazione è già prenotata per questa data");
                });
    }

    private void checkUserBookingConflict(Long userId, LocalDate date) {
        bookingRepository.findActiveBookingByUserAndDate(userId, date)
                .ifPresent(b -> {
                    throw new BookingException("Hai già una prenotazione attiva per questa data");
                });
    }

    private BookingDTO convertToDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getFullName())
                .userEmail(booking.getUser().getEmail())
                .deskId(booking.getDesk().getId())
                .deskNumber(booking.getDesk().getDeskNumber())
                .floorId(booking.getDesk().getFloor().getId())
                .floorName(booking.getDesk().getFloor().getName())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .type(booking.getType())
                .notes(booking.getNotes())
                .checkedInAt(booking.getCheckedInAt())
                .checkedOutAt(booking.getCheckedOutAt())
                .createdAt(booking.getCreatedAt())
                .build();
    }

    private BookingDetailDTO convertToDetailDTO(Booking booking) {
        return BookingDetailDTO.builder()
                .id(booking.getId())
                .bookingDate(booking.getBookingDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .type(booking.getType())
                .notes(booking.getNotes())
                .checkedInAt(booking.getCheckedInAt())
                .checkedOutAt(booking.getCheckedOutAt())
                .createdAt(booking.getCreatedAt())
                .cancellationReason(booking.getCancellationReason())
                .build();
    }
}
