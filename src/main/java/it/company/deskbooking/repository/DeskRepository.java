package it.company.deskbooking.repository;

import it.company.deskbooking.model.Desk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeskRepository extends JpaRepository<Desk, Long> {

    List<Desk> findByFloorId(Long floorId);

    List<Desk> findByDepartmentId(Long departmentId);

    List<Desk> findByActiveTrue();

    Optional<Desk> findByDeskNumberAndFloorId(String deskNumber, Long floorId);

    @Query("SELECT d FROM Desk d " +
           "WHERE d.floor.id = :floorId " +
           "AND d.active = true " +
           "AND d.status = 'AVAILABLE' " +
           "ORDER BY d.deskNumber")
    List<Desk> findAvailableDesksByFloor(@Param("floorId") Long floorId);

    @Query("SELECT d FROM Desk d " +
           "WHERE d.department.id = :departmentId " +
           "AND d.active = true " +
           "AND d.status = 'AVAILABLE' " +
           "ORDER BY d.deskNumber")
    List<Desk> findAvailableDesksByDepartment(@Param("departmentId") Long departmentId);

    @Query("SELECT d FROM Desk d " +
           "WHERE d.id NOT IN (" +
           "  SELECT b.desk.id FROM Booking b " +
           "  WHERE b.bookingDate = :date " +
           "  AND b.status IN ('ACTIVE', 'CHECKED_IN')" +
           ") " +
           "AND d.active = true " +
           "AND d.status = 'AVAILABLE' " +
           "ORDER BY d.floor.floorNumber, d.deskNumber")
    List<Desk> findAvailableDesksForDate(@Param("date") LocalDate date);

    @Query("SELECT d FROM Desk d " +
           "WHERE d.floor.id = :floorId " +
           "AND d.id NOT IN (" +
           "  SELECT b.desk.id FROM Booking b " +
           "  WHERE b.bookingDate = :date " +
           "  AND b.status IN ('ACTIVE', 'CHECKED_IN')" +
           ") " +
           "AND d.active = true " +
           "AND d.status = 'AVAILABLE' " +
           "ORDER BY d.deskNumber")
    List<Desk> findAvailableDesksForDateAndFloor(
        @Param("date") LocalDate date, 
        @Param("floorId") Long floorId
    );

    @Query("SELECT d FROM Desk d " +
           "WHERE d.department.id = :departmentId " +
           "AND d.id NOT IN (" +
           "  SELECT b.desk.id FROM Booking b " +
           "  WHERE b.bookingDate = :date " +
           "  AND b.status IN ('ACTIVE', 'CHECKED_IN')" +
           ") " +
           "AND d.active = true " +
           "AND d.status = 'AVAILABLE' " +
           "ORDER BY d.deskNumber")
    List<Desk> findAvailableDesksForDateAndDepartment(
        @Param("date") LocalDate date, 
        @Param("departmentId") Long departmentId
    );

    @Query("SELECT COUNT(d) FROM Desk d " +
           "WHERE d.floor.id = :floorId " +
           "AND d.active = true")
    Long countByFloorId(@Param("floorId") Long floorId);

    @Query("SELECT COUNT(d) FROM Desk d " +
           "WHERE d.department.id = :departmentId " +
           "AND d.active = true")
    Long countByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT d FROM Desk d " +
           "LEFT JOIN FETCH d.bookings b " +
           "WHERE d.id = :deskId " +
           "AND b.bookingDate BETWEEN :startDate AND :endDate")
    Optional<Desk> findByIdWithBookingsInRange(
        @Param("deskId") Long deskId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
