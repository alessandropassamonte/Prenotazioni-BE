package it.company.deskbooking.repository;

import it.company.deskbooking.model.LockerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LockerAssignmentRepository extends JpaRepository<LockerAssignment, Long> {

    List<LockerAssignment> findByUserId(Long userId);

    List<LockerAssignment> findByLockerId(Long lockerId);

    @Query("SELECT la FROM LockerAssignment la " +
           "WHERE la.user.id = :userId " +
           "AND la.status = 'ACTIVE' " +
           "AND la.startDate <= :date " +
           "AND (la.endDate IS NULL OR la.endDate >= :date)")
    Optional<LockerAssignment> findActiveAssignmentByUserAndDate(
        @Param("userId") Long userId,
        @Param("date") LocalDate date
    );

    @Query("SELECT la FROM LockerAssignment la " +
           "WHERE la.locker.id = :lockerId " +
           "AND la.status = 'ACTIVE' " +
           "AND la.startDate <= :date " +
           "AND (la.endDate IS NULL OR la.endDate >= :date)")
    Optional<LockerAssignment> findActiveAssignmentByLockerAndDate(
        @Param("lockerId") Long lockerId,
        @Param("date") LocalDate date
    );

    @Query("SELECT la FROM LockerAssignment la " +
           "WHERE la.status = 'ACTIVE' " +
           "AND la.startDate <= :date " +
           "AND (la.endDate IS NULL OR la.endDate >= :date)")
    List<LockerAssignment> findAllActiveAssignmentsForDate(@Param("date") LocalDate date);

    @Query("SELECT la FROM LockerAssignment la " +
           "WHERE la.locker.floor.id = :floorId " +
           "AND la.status = 'ACTIVE' " +
           "AND la.startDate <= :date " +
           "AND (la.endDate IS NULL OR la.endDate >= :date)")
    List<LockerAssignment> findActiveAssignmentsByFloorAndDate(
        @Param("floorId") Long floorId,
        @Param("date") LocalDate date
    );

    @Query("SELECT la FROM LockerAssignment la " +
           "WHERE la.user.id = :userId " +
           "AND la.status = 'ACTIVE'")
    List<LockerAssignment> findActiveAssignmentsByUser(@Param("userId") Long userId);

    @Query("SELECT la FROM LockerAssignment la " +
           "LEFT JOIN FETCH la.user " +
           "LEFT JOIN FETCH la.locker l " +
           "LEFT JOIN FETCH l.floor " +
           "WHERE la.status = 'ACTIVE' " +
           "AND la.startDate <= :date " +
           "AND (la.endDate IS NULL OR la.endDate >= :date)")
    List<LockerAssignment> findAllActiveAssignmentsWithDetailsForDate(@Param("date") LocalDate date);
}
