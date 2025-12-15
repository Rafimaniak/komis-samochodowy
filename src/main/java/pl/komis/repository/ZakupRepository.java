package pl.komis.repository;

import pl.komis.model.Zakup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ZakupRepository extends JpaRepository<Zakup, Long> {

    List<Zakup> findByKlientId(Long klientId);
    List<Zakup> findByPracownikId(Long pracownikId);
    List<Zakup> findByDataZakupuBetween(LocalDate startDate, LocalDate endDate);
    boolean existsBySamochodId(Long samochodId);
    boolean existsBySamochodIdAndKlientId(Long samochodId, Long klientId);

    // Nowe metody wykorzystujące procedury/funkcje z bazy
    @Transactional
    @Procedure(name = "dodaj_zakup")
    Long dodajZakupZProcedury(
            @Param("p_nowy_id_zakupu") Long nowyIdZakupu,  // OUT parameter musi być pierwszy
            @Param("p_id_samochodu") Long samochodId,
            @Param("p_id_klienta") Long klientId,
            @Param("p_id_pracownika") Long pracownikId,
            @Param("p_cena_bazowa") BigDecimal cenaBazowa,
            @Param("p_wykorzystane_saldo") BigDecimal wykorzystaneSaldo
    );

    @Transactional
    @Modifying
    @Query(value = "CALL usun_zakup(:idZakupu)", nativeQuery = true)
    void usunZakupZProcedury(@Param("idZakupu") Long idZakupu);

    @Query(value = "SELECT * FROM znajdz_zakupy_klienta(:idKlienta)", nativeQuery = true)
    List<Object[]> znajdzZakupyKlientaZProcedury(@Param("idKlienta") Long idKlienta);
}