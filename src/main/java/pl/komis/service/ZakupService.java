package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.*;
import pl.komis.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZakupService {

    private final ZakupRepository zakupRepository;
    private final KlientRepository klientRepository;
    private final SamochodService samochodService;
    private final KlientService klientService;
    private final PracownikService pracownikService;
    private final UserService userService;

    public List<Zakup> findAll() {
        return zakupRepository.findAll();
    }

    public Zakup createZakupFromSprzedaz(Long samochodId, Long userId, Long pracownikId, BigDecimal cenaBazowa) {
        // Pobierz samochód
        Samochod samochod = samochodService.findById(samochodId)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        // Pobierz użytkownika
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Pobierz klienta powiązanego z użytkownikiem
        Klient klient = user.getKlient();
        if (klient == null) {
            throw new RuntimeException("Użytkownik nie ma powiązanego klienta");
        }

        // Pobierz pracownika
        Pracownik pracownik = pracownikService.findById(pracownikId)
                .orElseThrow(() -> new RuntimeException("Pracownik nie znaleziony"));

        // Pobierz aktualny rabat klienta
        BigDecimal rabatProcent = klient.getAktualnyRabat();

        // Oblicz cenę z rabatem
        BigDecimal cenaZRabatem;
        if (rabatProcent.compareTo(BigDecimal.ZERO) > 0) {
            // Oblicz mnożnik rabatu: 1 - (rabat%/100)
            BigDecimal mnoznikRabatu = BigDecimal.ONE
                    .subtract(rabatProcent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            // Oblicz cenę z rabatem
            cenaZRabatem = cenaBazowa.multiply(mnoznikRabatu)
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            cenaZRabatem = cenaBazowa;
        }

        // Utwórz nowy zakup
        Zakup zakup = new Zakup();
        zakup.setSamochod(samochod);
        zakup.setKlient(klient);
        zakup.setPracownik(pracownik);
        zakup.setDataZakupu(LocalDate.now());
        zakup.setCenaBazowa(cenaBazowa);
        zakup.setCenaZakupu(cenaZRabatem);
        zakup.setZastosowanyRabat(rabatProcent);

        // Zapisz zakup (trigger w bazie zaktualizuje rabat klienta)
        return zakupRepository.save(zakup);
    }

    public Zakup save(Zakup zakup) {
        // Jeśli nie ma ceny bazowej, użyj ceny zakupu jako ceny bazowej
        if (zakup.getCenaBazowa() == null && zakup.getCenaZakupu() != null) {
            zakup.setCenaBazowa(zakup.getCenaZakupu());
        }
        return zakupRepository.save(zakup);
    }

    public void remove(Long id) {
        // Najpierw pobierz zakup, żeby trigger wiedział o kliencie
        Zakup zakup = zakupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zakup nie znaleziony"));
        zakupRepository.delete(zakup);
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

    // Metoda pomocnicza do obliczania ceny z rabatem dla klienta
    public BigDecimal obliczCeneZRabatem(BigDecimal cenaBazowa, Long klientId) {
        // Pobierz klienta
        Klient klient = klientRepository.findById(klientId)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));

        // Pobierz rabat klienta
        BigDecimal rabatProcent = klient.getAktualnyRabat();

        if (rabatProcent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal mnoznikRabatu = BigDecimal.ONE
                    .subtract(rabatProcent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            return cenaBazowa.multiply(mnoznikRabatu)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return cenaBazowa;
    }

    // Metoda do pobierania rabatu klienta
    public BigDecimal pobierzRabatKlienta(Long klientId) {
        Klient klient = klientRepository.findById(klientId)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));

        return klient.getAktualnyRabat();
    }
}