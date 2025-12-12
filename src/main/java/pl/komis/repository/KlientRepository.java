package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Klient;

import java.math.BigDecimal;
import java.util.Optional;

public interface KlientRepository extends JpaRepository<Klient, Long> {

    Optional<Klient> findByEmail(String email);

    // ZMIANA: aktualny_rabat → procent_premii
    @Query(value = "SELECT k.procent_premii FROM klient k WHERE k.id = :klientId", nativeQuery = true)
    Optional<BigDecimal> findProcentPremiiById(@Param("klientId") Long klientId);

    // Nowa metoda: pobierz saldo premii
    @Query(value = "SELECT k.saldo_premii FROM klient k WHERE k.id = :klientId", nativeQuery = true)
    Optional<BigDecimal> findSaldoPremiiById(@Param("klientId") Long klientId);

    @Query(value = "SELECT k.liczba_zakupow FROM klient k WHERE k.id = :klientId", nativeQuery = true)
    Optional<Integer> findLiczbaZakupowById(@Param("klientId") Long klientId);

    // Usuń starą metodę - funkcja oblicz_rabat już nie istnieje
    // @Query(value = "SELECT pobierz_rabat_dla_klienta(:klientId)", nativeQuery = true)
    // BigDecimal getRabatDlaKlientaNative(@Param("klientId") Long klientId);

    @Query("SELECT k FROM Klient k WHERE k.id = :id")
    Optional<Klient> findByIdWithZakupy(@Param("id") Long id);

    // Nowa metoda: wywołaj funkcję PostgreSQL do obliczenia procentu premii
    @Query(value = "SELECT oblicz_procent_premii(:liczbaZakupow)", nativeQuery = true)
    BigDecimal obliczProcentPremiiNative(@Param("liczbaZakupow") Long liczbaZakupow);
    @Query("SELECT COALESCE(SUM(z.naliczonaPremia), 0) FROM Zakup z WHERE z.klient.id = :klientId")
    BigDecimal sumNaliczonaPremiaByKlientId(@Param("klientId") Long klientId);

    @Query("SELECT COALESCE(SUM(z.wykorzystaneSaldo), 0) FROM Zakup z WHERE z.klient.id = :klientId")
    BigDecimal sumWykorzystaneSaldoByKlientId(@Param("klientId") Long klientId);
}