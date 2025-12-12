package pl.komis.repository;

import pl.komis.model.Zakup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ZakupRepository extends JpaRepository<Zakup, Long> {

    List<Zakup> findByKlientId(Long klientId);

    List<Zakup> findByPracownikId(Long pracownikId);

    List<Zakup> findByDataZakupuBetween(LocalDate startDate, LocalDate endDate);

    // Dodaj te metody jeśli ich nie ma:
    boolean existsBySamochodId(Long samochodId);

    boolean existsBySamochodIdAndKlientId(Long samochodId, Long klientId);

    @Query("SELECT YEAR(z.dataZakupu) as rok, MONTH(z.dataZakupu) as miesiac, " +
            "COUNT(z) as liczbaZakupow, SUM(z.cenaZakupu) as sumaSprzedazy " +
            "FROM Zakup z " +
            "WHERE YEAR(z.dataZakupu) = :rok " +
            "GROUP BY YEAR(z.dataZakupu), MONTH(z.dataZakupu) " +
            "ORDER BY rok, miesiac")
    List<Object[]> findMonthlyStatistics(@Param("rok") int rok);

    @Query("SELECT YEAR(z.dataZakupu) as rok, " +
            "COUNT(z) as liczbaZakupow, SUM(z.cenaZakupu) as sumaSprzedazy " +
            "FROM Zakup z " +
            "GROUP BY YEAR(z.dataZakupu) " +
            "ORDER BY rok")
    List<Object[]> findYearlyStatistics();

    @Query("SELECT z.klient, COUNT(z) as liczbaZakupow, SUM(z.cenaZakupu) as sumaWydatkow " +
            "FROM Zakup z " +
            "GROUP BY z.klient " +
            "ORDER BY sumaWydatkow DESC")
    List<Object[]> findTopKlienci(@Param("limit") int limit);

    @Query("SELECT z.pracownik, COUNT(z) as liczbaSprzedazy, SUM(z.cenaZakupu) as sumaSprzedazy " +
            "FROM Zakup z " +
            "GROUP BY z.pracownik " +
            "ORDER BY sumaSprzedazy DESC")
    List<Object[]> findTopPracownicy(@Param("limit") int limit);
}