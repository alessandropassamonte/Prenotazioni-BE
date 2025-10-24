package it.company.deskbooking.repository;

import it.company.deskbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeId(String employeeId);

    List<User> findByActiveTrue();

    List<User> findByDepartmentId(Long departmentId);

    @Query("SELECT u FROM User u WHERE u.active = true AND u.department.id = :departmentId")
    List<User> findActiveDepartmentUsers(Long departmentId);

    @Query("SELECT u FROM User u WHERE u.workType = 'TURNISTA' AND u.active = true")
    List<User> findAllTurnisti();

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.department.id = :departmentId AND u.active = true")
    Long countActiveUsersByDepartment(Long departmentId);
}
