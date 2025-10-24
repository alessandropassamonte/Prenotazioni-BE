package it.company.deskbooking.repository;

import it.company.deskbooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByDeskId(Long deskId);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.user.id = :userId " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate " +
           "ORDER BY b.bookingDate DESC")
    List<Booking> findByUserIdAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT b FROM Booking b " +
           "WHERE b.desk.id = :deskId " +
           "AND b.bookingDate = :date " +
           "AND b.status IN ('ACTIVE', 'CHECKED_IN')")
    Optional<Booking> findActiveBookingByDeskAndDate(
        @Param("deskId") Long deskId,
        @Param("date") LocalDate date
    );

    @Query("SELECT b FROM Booking b " +
           "WHERE b.user.id = :userId " +
           "AND b.bookingDate = :date " +
           "AND b.status IN ('ACTIVE', 'CHECKED_IN')")
    Optional<Booking> findActiveBookingByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("SELECT b FROM Booking b " +
           "WHERE b.bookingDate = :date " +
           "AND b.status IN ('ACTIVE', 'CHECKED_IN') " +
           "ORDER BY b.desk.floor.floorNumber, b.desk.deskNumber")
    List<Booking> findActiveBookingsForDate(@Param("date") LocalDate date);

    @Query("SELECT b FROM Booking b " +
           "WHERE b.desk.floor.id = :floorId " +
           "AND b.bookingDate = :date " +
           "AND b.status IN ('ACTIVE', 'CHECKED_IN') " +
           "ORDER BY b.desk.deskNumber")
    List<Booking> findActiveBookingsForDateAndFloor(
        @Param("date") LocalDate date,
        @Param("floorId") Long floorId
    );

    @Query("SELECT b FROM Booking b " +
           "WHERE b.user.id = :userId " +
           "AND b.bookingDate >= :date " +
           "AND b.status = 'ACTIVE' " +
           "ORDER BY b.bookingDate ASC")
    List<Booking> findUpcomingBookingsByUser(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("SELECT COUNT(b) FROM Booking b " +
           "WHERE b.desk.id = :deskId " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate " +
           "AND b.status NOT IN ('CANCELLED', 'NO_SHOW')")
    Long countBookingsByDeskInRange(
        @Param("deskId") Long deskId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(b) FROM Booking b " +
           "WHERE b.user.id = :userId " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate " +
           "AND b.status NOT IN ('CANCELLED', 'NO_SHOW')")
    Long countBookingsByUserInRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.desk d " +
           "LEFT JOIN FETCH d.floor " +
           "WHERE b.bookingDate = :date " +
           "AND b.status IN ('ACTIVE', 'CHECKED_IN')")
    List<Booking> findActiveBookingsWithDetailsForDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(DISTINCT b.desk.id) FROM Booking b " +
           "WHERE b.bookingDate = :date " +
           "AND b.status IN ('ACTIVE', 'CHECKED_IN')")
    Long countOccupiedDesksForDate(@Param("date") LocalDate date);
}
