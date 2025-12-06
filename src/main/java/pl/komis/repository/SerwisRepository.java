package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Serwis;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SerwisRepository extends JpaRepository<Serwis, Long> {

    // Podstawowe metody
    List<Serwis> findByDataSerwisuBetween(LocalDate od, LocalDate do_);

    @Query("SELECT s FROM Serwis s WHERE s.samochod.id = :samochodId")
    List<Serwis> findBySamochodId(@Param("samochodId") Long samochodId);

    @Query("SELECT s FROM Serwis s WHERE s.pracownik.id = :pracownikId")
    List<Serwis> findByPracownikId(@Param("pracownikId") Long pracownikId);

    // PROCEDURY - POPRAWIONE TYPY
    @Procedure(procedureName = "create_service")
    Long createService(
            @Param("p_id_samochodu") Long idSamochodu,
            @Param("p_id_pracownika") Long idPracownika,
            @Param("p_opis_uslugi") String opisUslugi,
            @Param("p_koszt") BigDecimal koszt,  // ZMIANA: Double -> BigDecimal
            @Param("p_data_serwisu") LocalDate dataSerwisu
    );

    @Procedure(procedureName = "update_service")
    void updateService(
            @Param("p_id") Long id,
            @Param("p_id_samochodu") Long idSamochodu,
            @Param("p_id_pracownika") Long idPracownika,
            @Param("p_opis_uslugi") String opisUslugi,
            @Param("p_koszt") BigDecimal koszt,  // ZMIANA: Double -> BigDecimal
            @Param("p_data_serwisu") LocalDate dataSerwisu
    );

    @Procedure(procedureName = "delete_service")
    void deleteService(@Param("p_id") Long id);

    @Procedure(procedureName = "reserve_service")
    Long reserveService(
            @Param("p_id_samochodu") Long idSamochodu,
            @Param("p_id_pracownika") Long idPracownika,
            @Param("p_opis_uslugi") String opisUslugi,
            @Param("p_szacowany_koszt") BigDecimal szacowanyKoszt,  // ZMIANA
            @Param("p_data_serwisu") LocalDate dataSerwisu
    );

    @Procedure(procedureName = "complete_service")
    void completeService(
            @Param("p_service_id") Long serviceId,
            @Param("p_zreczynisty_koszt") BigDecimal rzeczywistyKoszt,  // ZMIANA
            @Param("p_dodatkowe_uwagi") String dodatkoweUwagi
    );

    @Procedure(procedureName = "cancel_service")
    void cancelService(@Param("p_service_id") Long serviceId);

    // Statystyki
    @Query("SELECT COUNT(s) FROM Serwis s WHERE s.koszt IS NULL")
    long countReservedServices();

    @Query("SELECT COUNT(s) FROM Serwis s WHERE s.koszt IS NOT NULL")
    long countCompletedServices();

    @Query("SELECT COALESCE(SUM(s.koszt), 0) FROM Serwis s WHERE s.koszt IS NOT NULL")
    BigDecimal getTotalServiceCost();
}