package it.company.deskbooking.repository;

import it.company.deskbooking.model.CompanyHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, Long> {

    /**
     * Trova tutte le festività attive
     */
    List<CompanyHoliday> findByActiveTrue();

    /**
     * Trova tutte le festività in un range di date
     * Include solo le festività attive presenti nel range
     */
    @Query("SELECT h FROM CompanyHoliday h WHERE h.date BETWEEN :startDate AND :endDate AND h.active = true ORDER BY h.date")
    List<CompanyHoliday> findByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Trova festività per data specifica
     */
    Optional<CompanyHoliday> findByDateAndActiveTrue(LocalDate date);

    /**
     * Trova festività ricorrenti attive (per applicarle agli anni successivi)
     */
    List<CompanyHoliday> findByRecurringTrueAndActiveTrue();

    /**
     * Verifica se una data è un giorno non lavorativo
     */
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM CompanyHoliday h WHERE h.date = :date AND h.active = true")
    boolean isHoliday(@Param("date") LocalDate date);

    List<CompanyHoliday> findByDateBetweenAndActiveTrue(LocalDate startDate, LocalDate endDate);

    /**
     * Conta le festività attive
     */
    long countByActiveTrue();

    /**
     * Elimina festività vecchie (più di X anni fa) - solo non ricorrenti
     */
    @Query("DELETE FROM CompanyHoliday h WHERE h.date < :beforeDate AND h.recurring = false")
    void deleteOldNonRecurringHolidays(@Param("beforeDate") LocalDate beforeDate);
}




