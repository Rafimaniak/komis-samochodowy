package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Serwis;

import java.time.LocalDate;
import java.util.List;

public interface SerwisRepository extends JpaRepository<Serwis, Long> {

    List<Serwis> findByDataSerwisuBetween(LocalDate od, LocalDate do_);

    // POPRAWIONE METODY Z @Query
    @Query("SELECT s FROM Serwis s WHERE s.samochod.id = :samochodId")
    List<Serwis> findBySamochodId(@Param("samochodId") Long samochodId);

    @Query("SELECT s FROM Serwis s WHERE s.pracownik.id = :pracownikId")
    List<Serwis> findByPracownikId(@Param("pracownikId") Long pracownikId);
}