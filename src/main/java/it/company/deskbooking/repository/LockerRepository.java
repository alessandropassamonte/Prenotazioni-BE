package it.company.deskbooking.repository;

import it.company.deskbooking.model.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    List<Locker> findByFloorId(Long floorId);

    List<Locker> findByActiveTrue();

    Optional<Locker> findByLockerNumberAndFloorId(String lockerNumber, Long floorId);

    @Query("SELECT l FROM Locker l " +
           "WHERE l.floor.id = :floorId " +
           "AND l.type = :type " +
           "AND l.active = true " +
           "ORDER BY l.lockerNumber")
    List<Locker> findByFloorIdAndType(@Param("floorId") Long floorId, @Param("type") Locker.LockerType type);

    @Query("SELECT l FROM Locker l " +
           "WHERE l.floor.id = :floorId " +
           "AND l.status = 'FREE' " +
           "AND l.active = true " +
           "ORDER BY l.lockerNumber")
    List<Locker> findFreeLockersByFloor(@Param("floorId") Long floorId);

    @Query("SELECT l FROM Locker l " +
           "WHERE l.type = 'TURNISTI' " +
           "AND l.status = 'FREE' " +
           "AND l.active = true " +
           "ORDER BY l.floor.floorNumber, l.lockerNumber")
    List<Locker> findFreeTurnistiLockers();

    @Query("SELECT l FROM Locker l " +
           "WHERE l.type = 'FREE' " +
           "AND l.status = 'FREE' " +
           "AND l.active = true " +
           "ORDER BY l.floor.floorNumber, l.lockerNumber")
    List<Locker> findFreeRegularLockers();

    @Query("SELECT COUNT(l) FROM Locker l " +
           "WHERE l.floor.id = :floorId " +
           "AND l.type = :type " +
           "AND l.active = true")
    Long countByFloorAndType(@Param("floorId") Long floorId, @Param("type") Locker.LockerType type);

    @Query("SELECT COUNT(l) FROM Locker l " +
           "WHERE l.floor.id = :floorId " +
           "AND l.status = 'FREE' " +
           "AND l.active = true")
    Long countFreeLockersByFloor(@Param("floorId") Long floorId);

    @Query("SELECT l FROM Locker l " +
           "LEFT JOIN FETCH l.assignments a " +
           "WHERE l.id = :lockerId " +
           "AND a.status = 'ACTIVE'")
    Optional<Locker> findByIdWithActiveAssignments(@Param("lockerId") Long lockerId);
}
