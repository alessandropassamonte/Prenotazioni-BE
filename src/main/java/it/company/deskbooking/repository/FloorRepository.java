package it.company.deskbooking.repository;

import it.company.deskbooking.model.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {

    List<Floor> findByActiveTrue();

    Optional<Floor> findByFloorNumber(Integer floorNumber);

    Optional<Floor> findByCode(String code);

    @Query("SELECT f FROM Floor f LEFT JOIN FETCH f.desks WHERE f.id = :id")
    Optional<Floor> findByIdWithDesks(Long id);

    @Query("SELECT f FROM Floor f LEFT JOIN FETCH f.departments WHERE f.id = :id")
    Optional<Floor> findByIdWithDepartments(Long id);

    @Query("SELECT f FROM Floor f " +
           "LEFT JOIN FETCH f.desks d " +
           "LEFT JOIN FETCH f.departments dep " +
           "WHERE f.active = true " +
           "ORDER BY f.floorNumber")
    List<Floor> findAllActiveWithDetails();
}
