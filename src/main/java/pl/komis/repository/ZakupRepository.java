package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Zakup;

import java.time.LocalDate;
import java.util.List;

public interface ZakupRepository extends JpaRepository<Zakup, Long> {

    List<Zakup> findByDataZakupuBetween(LocalDate od, LocalDate do_);

    // POPRAWIONE METODY Z @Query
    @Query("SELECT z FROM Zakup z WHERE z.klient.id = :klientId")
    List<Zakup> findByKlientId(@Param("klientId") Long klientId);

    @Query("SELECT z FROM Zakup z WHERE z.pracownik.id = :pracownikId")
    List<Zakup> findByPracownikId(@Param("pracownikId") Long pracownikId);
}