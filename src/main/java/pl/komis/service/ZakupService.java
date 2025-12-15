package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.*;
import pl.komis.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZakupService {

    private final ZakupRepository zakupRepository;
    private final KlientRepository klientRepository;
    private final SamochodService samochodService;
    private final PracownikService pracownikService;
    private final UserService userService;
    private final EntityManager entityManager; // Wstrzyknięcie EntityManager

    @Transactional
    public Long createZakupZSaldem(Long samochodId, Long userId, Long pracownikId,
                                   BigDecimal cenaBazowa, BigDecimal wykorzystaneSaldo) {

        // Walidacje pozostają w Javie
        Samochod samochod = samochodService.findById(samochodId)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        Klient klient = user.getKlient();
        if (klient == null) {
            throw new RuntimeException("Użytkownik nie ma powiązanego klienta");
        }

        Pracownik pracownik = pracownikService.findById(pracownikId)
                .orElseThrow(() -> new RuntimeException("Pracownik nie znaleziony"));

        // Walidacja: wykorzystane saldo nie może przekraczać ceny samochodu
        if (wykorzystaneSaldo != null && wykorzystaneSaldo.compareTo(cenaBazowa) > 0) {
            throw new RuntimeException("Wykorzystane saldo nie może przekraczać ceny samochodu");
        }

        // Walidacja: sprawdź czy klient ma wystarczające saldo
        BigDecimal faktycznieWykorzystane = (wykorzystaneSaldo != null) ? wykorzystaneSaldo : BigDecimal.ZERO;
        if (faktycznieWykorzystane.compareTo(BigDecimal.ZERO) > 0) {
            if (klient.getSaldoPremii() == null ||
                    klient.getSaldoPremii().compareTo(faktycznieWykorzystane) < 0) {
                throw new RuntimeException("Niewystarczające saldo premii. Masz: " +
                        klient.getSaldoPremii() + " zł, a potrzebujesz: " +
                        faktycznieWykorzystane + " zł");
            }
        }

        // UŻYCIE PROCEDURY Z BAZY DANYCH przez EntityManager
        try {
            StoredProcedureQuery query = entityManager
                    .createStoredProcedureQuery("public.dodaj_zakup");

            // Rejestracja parametrów w odpowiedniej kolejności
            query.registerStoredProcedureParameter(1, Long.class, ParameterMode.OUT); // p_nowy_id_zakupu
            query.registerStoredProcedureParameter(2, Long.class, ParameterMode.IN);  // p_id_samochodu
            query.registerStoredProcedureParameter(3, Long.class, ParameterMode.IN);  // p_id_klienta
            query.registerStoredProcedureParameter(4, Long.class, ParameterMode.IN);  // p_id_pracownika
            query.registerStoredProcedureParameter(5, BigDecimal.class, ParameterMode.IN); // p_cena_bazowa
            query.registerStoredProcedureParameter(6, BigDecimal.class, ParameterMode.IN); // p_wykorzystane_saldo

            // Ustawienie parametrów IN
            query.setParameter(2, samochodId);
            query.setParameter(3, klient.getId());
            query.setParameter(4, pracownikId);
            query.setParameter(5, cenaBazowa);
            query.setParameter(6, faktycznieWykorzystane);

            // Wykonanie procedury
            query.execute();

            // Pobranie wartości OUT
            Long noweIdZakupu = (Long) query.getOutputParameterValue(1);

            // Zmiana statusu samochodu
            samochod.setStatus("SPRZEDANY");
            samochod.setZarezerwowanyPrzez(null);
            samochod.setDataRezerwacji(null);
            samochodService.save(samochod);

            return noweIdZakupu;

        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas tworzenia zakupu przez procedurę: " + e.getMessage(), e);
        }
    }

    // ==================== METODY ODCZYTU ====================

    public List<Zakup> findAll() {
        return zakupRepository.findAll();
    }

    public Optional<Zakup> findById(Long id) {
        return zakupRepository.findById(id);
    }

    public Zakup getById(Long id) {
        return zakupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zakup nie znaleziony"));
    }

    public List<Zakup> findByKlientId(Long id) {
        return zakupRepository.findByKlientId(id);
    }

    public List<Zakup> findByPracownikId(Long id) {
        return zakupRepository.findByPracownikId(id);
    }

    public List<Zakup> findByDateRange(LocalDate od, LocalDate do_) {
        return zakupRepository.findByDataZakupuBetween(od, do_);
    }

    // ==================== METODY ZAPISU ====================

    public Zakup save(Zakup zakup) {
        return zakupRepository.save(zakup);
    }

    // ==================== METODY USUWANIA ====================

    @Transactional
    public void remove(Long id) {
        try {
            zakupRepository.deleteById(id); // Standardowe usunięcie (trigger zadziała)
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas usuwania zakupu: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void delete(Long id) {
        remove(id); // Alias dla remove
    }

    // ==================== METODY POMOCNICZE ====================

    public BigDecimal pobierzSaldoKlienta(Long klientId) {
        Klient klient = klientRepository.findById(klientId)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));
        return klient.getSaldoPremii();
    }

    public boolean czyMozeWykorzystacSaldo(Long klientId, BigDecimal kwota) {
        if (kwota == null || kwota.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        Klient klient = klientRepository.findById(klientId)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));

        return klient.getSaldoPremii() != null &&
                klient.getSaldoPremii().compareTo(kwota) >= 0;
    }

    // ==================== METODY STATYSTYCZNE ====================

    public BigDecimal getSumaWydatkowKlienta(Long klientId) {
        List<Zakup> zakupy = findByKlientId(klientId);
        return zakupy.stream()
                .map(Zakup::getCenaZakupu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSumaWykorzystanegoSaldaKlienta(Long klientId) {
        List<Zakup> zakupy = findByKlientId(klientId);
        return zakupy.stream()
                .map(Zakup::getWykorzystaneSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getLiczbaZakupowKlienta(Long klientId) {
        List<Zakup> zakupy = findByKlientId(klientId);
        return zakupy.size();
    }

    // ==================== METODY WALIDACYJNE ====================

    public boolean isSamochodKupiony(Long samochodId) {
        return zakupRepository.existsBySamochodId(samochodId);
    }

    public boolean isSamochodKupionyPrzezKlienta(Long samochodId, Long klientId) {
        return zakupRepository.existsBySamochodIdAndKlientId(samochodId, klientId);
    }
}