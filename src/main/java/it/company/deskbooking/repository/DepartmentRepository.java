package it.company.deskbooking.repository;

import it.company.deskbooking.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByActiveTrue();

    Optional<Department> findByCode(String code);

    List<Department> findByFloorId(Long floorId);

    @Query("SELECT d FROM Department d " +
           "WHERE d.active = true AND d.floor.id = :floorId " +
           "ORDER BY d.name")
    List<Department> findActiveByFloorId(Long floorId);

    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.desks WHERE d.id = :id")
    Optional<Department> findByIdWithDesks(Long id);

    @Query("SELECT COUNT(d) FROM Desk d WHERE d.department.id = :departmentId AND d.active = true")
    Long countActiveDesks(Long departmentId);
}
