package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.*;
import pl.komis.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public Zakup createZakupZSaldem(Long samochodId, Long userId, Long pracownikId,
                                    BigDecimal cenaBazowa, BigDecimal wykorzystaneSaldo) {
        // Pobierz samochód
        Samochod samochod = samochodService.findById(samochodId)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        // Pobierz użytkownika
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Pobierz klienta
        Klient klient = user.getKlient();
        if (klient == null) {
            throw new RuntimeException("Użytkownik nie ma powiązanego klienta");
        }

        // Pobierz pracownika
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

        // Oblicz cenę końcową (trigger w bazie również to zrobi, ale potrzebujemy w Javie)
        BigDecimal cenaKoncowa = cenaBazowa.subtract(faktycznieWykorzystane);

        // Pobierz aktualny procent premii klienta
        BigDecimal procentPremii = klient.getProcentPremii();

        // Oblicz premię od tego zakupu (procent od ceny bazowej)
        // UWAGA: Trigger w bazie również to obliczy i zapisze!
        BigDecimal naliczonaPremia = BigDecimal.ZERO;
        if (procentPremii != null && procentPremii.compareTo(BigDecimal.ZERO) > 0) {
            naliczonaPremia = cenaBazowa.multiply(procentPremii)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        // Utwórz nowy zakup
        Zakup zakup = Zakup.builder()
                .samochod(samochod)
                .klient(klient)
                .pracownik(pracownik)
                .dataZakupu(LocalDate.now())
                .cenaBazowa(cenaBazowa)
                .cenaZakupu(cenaKoncowa)
                .zastosowanyRabat(procentPremii)
                .naliczonaPremia(naliczonaPremia)
                .wykorzystaneSaldo(faktycznieWykorzystane)
                .build();

        // NIE aktualizujemy ręcznie salda klienta - robi to trigger!
        // Trigger w bazie:
        // 1. Doda naliczoną premię do saldo_premii
        // 2. Odejmie wykorzystane_saldo
        // 3. Zaktualizuje procent_premii na następny zakup

        return zakupRepository.save(zakup); // Trigger zadziała przy zapisie!
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

    public void remove(Long id) {
        Zakup zakup = zakupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zakup nie znaleziony"));
        zakupRepository.delete(zakup);
    }

    public void delete(Long id) {
        zakupRepository.deleteById(id);
    }

    // ==================== METODY POMOCNICZE ====================

    // Metoda do pobierania salda klienta (do wyświetlania w UI)
    public BigDecimal pobierzSaldoKlienta(Long klientId) {
        Klient klient = klientRepository.findById(klientId)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));
        return klient.getSaldoPremii();
    }

    // Nowa metoda: Sprawdź czy klient może wykorzystać saldo
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

    public BigDecimal getSredniaWartoscZakupuKlienta(Long klientId) {
        List<Zakup> zakupy = findByKlientId(klientId);
        if (zakupy.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal suma = getSumaWydatkowKlienta(klientId);
        return suma.divide(BigDecimal.valueOf(zakupy.size()), 2, RoundingMode.HALF_UP);
    }

    // ==================== METODY WALIDACYJNE ====================

    public boolean isSamochodKupiony(Long samochodId) {
        // Sprawdź czy istnieje zakup dla tego samochodu
        return zakupRepository.existsBySamochodId(samochodId);
    }

    public boolean isSamochodKupionyPrzezKlienta(Long samochodId, Long klientId) {
        return zakupRepository.existsBySamochodIdAndKlientId(samochodId, klientId);
    }

    // ==================== METODY RAPORTOWE ====================

    public List<Object[]> getStatystykiMiesieczne(int rok) {
        return zakupRepository.findMonthlyStatistics(rok);
    }

    public List<Object[]> getStatystykiRoczne() {
        return zakupRepository.findYearlyStatistics();
    }

    public List<Object[]> getTopKlienci(int limit) {
        return zakupRepository.findTopKlienci(limit);
    }

    public List<Object[]> getTopPracownicy(int limit) {
        return zakupRepository.findTopPracownicy(limit);
    }
}