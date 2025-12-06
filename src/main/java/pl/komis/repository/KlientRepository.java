package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Klient;

import java.math.BigDecimal;
import java.util.Optional;

public interface KlientRepository extends JpaRepository<Klient, Long> {

    // Znajdź klienta po emailu
    Optional<Klient> findByEmail(String email);

    // Pobierz aktualny rabat klienta (używając native query do bezpośredniego odczytu z bazy)
    @Query(value = "SELECT k.aktualny_rabat FROM klienci k WHERE k.id = :klientId", nativeQuery = true)
    Optional<BigDecimal> findAktualnyRabatById(@Param("klientId") Long klientId);

    // Pobierz liczbę zakupów klienta
    @Query(value = "SELECT k.liczba_zakupow FROM klienci k WHERE k.id = :klientId", nativeQuery = true)
    Optional<Integer> findLiczbaZakupowById(@Param("klientId") Long klientId);

    // Wywołaj funkcję PostgreSQL do pobrania rabatu (alternatywna metoda)
    @Query(value = "SELECT pobierz_rabat_dla_klienta(:klientId)", nativeQuery = true)
    BigDecimal getRabatDlaKlientaNative(@Param("klientId") Long klientId);
    @Query("SELECT k FROM Klient k WHERE k.id = :id")
    Optional<Klient> findByIdWithZakupy(@Param("id") Long id);
}