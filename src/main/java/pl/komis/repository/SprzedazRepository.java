package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.komis.model.Sprzedaz;

import java.time.LocalDate;
import java.util.List;

public interface SprzedazRepository extends JpaRepository<Sprzedaz, Long> {

    // ZAKOMENTUJ WSZYSTKIE METODY
    // List<Sprzedaz> findByDataSprzedazyBetween(LocalDate od, LocalDate do_);
    // List<Sprzedaz> findByPracownikIdPracownika(Long idPracownika);
    // List<Sprzedaz> findByKlientIdKlienta(Long idKlienta);
    // List<Sprzedaz> findBySamochodIdSamochodu(Long idSamochodu);
}