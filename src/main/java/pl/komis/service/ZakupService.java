package pl.komis.service;

import pl.komis.model.Zakup;
import pl.komis.model.Samochod;
import pl.komis.model.Klient;
import pl.komis.repository.ZakupRepository;
import pl.komis.repository.SamochodRepository;
import pl.komis.repository.KlientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ZakupService {

    private final ZakupRepository zakupRepository;
    private final SamochodRepository samochodRepository;
    private final KlientRepository klientRepository;

    public ZakupService(ZakupRepository zakupRepository,
                        SamochodRepository samochodRepository,
                        KlientRepository klientRepository) {
        this.zakupRepository = zakupRepository;
        this.samochodRepository = samochodRepository;
        this.klientRepository = klientRepository;
    }

    // --- Odczyt wszystkich zakupów ---
    public List<Zakup> getAllZakupy() {
        return zakupRepository.findAll();
    }

    // --- Odczyt pojedynczego zakupu ---
    public Zakup getZakupById(Long id) {
        return zakupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono zakupu o ID: " + id));
    }

    // --- Dodawanie nowego zakupu ---
    public Zakup addZakup(Zakup zakup) {
        // Walidacja klienta
        if (zakup.getKlient() == null || zakup.getKlient().getId() == null) {
            throw new IllegalArgumentException("Zakup musi zawierać poprawnego klienta.");
        }
        Klient klient = klientRepository.findById(zakup.getKlient().getId())
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono klienta o ID: " + zakup.getKlient().getId()));

        // Walidacja samochodu
        if (zakup.getSamochod() == null || zakup.getSamochod().getId() == null) {
            throw new IllegalArgumentException("Zakup musi zawierać poprawny samochód.");
        }
        Samochod samochod = samochodRepository.findById(zakup.getSamochod().getId())
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono samochodu o ID: " + zakup.getSamochod().getId()));

        // Możemy np. ustawić domyślną cenę z samochodu, jeśli nie podano
        if (zakup.getCenaZakupu() == null) {
            zakup.setCenaZakupu(samochod.getCena());
        }

        zakup.setKlient(klient);
        zakup.setSamochod(samochod);

        return zakupRepository.save(zakup);
    }

    // --- Aktualizacja istniejącego zakupu ---
    public Zakup updateZakup(Long id, Zakup updatedZakup) {
        Zakup istniejący = zakupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Nie znaleziono zakupu o ID: " + id));

        if (updatedZakup.getDataZakupu() != null) {
            istniejący.setDataZakupu(updatedZakup.getDataZakupu());
        }
        if (updatedZakup.getCenaZakupu() != null) {
            istniejący.setCenaZakupu(updatedZakup.getCenaZakupu());
        }
        if (updatedZakup.getKlient() != null) {
            Klient klient = klientRepository.findById(updatedZakup.getKlient().getId())
                    .orElseThrow(() -> new NoSuchElementException("Nie znaleziono klienta o ID: " + updatedZakup.getKlient().getId()));
            istniejący.setKlient(klient);
        }
        if (updatedZakup.getSamochod() != null) {
            Samochod samochod = samochodRepository.findById(updatedZakup.getSamochod().getId())
                    .orElseThrow(() -> new NoSuchElementException("Nie znaleziono samochodu o ID: " + updatedZakup.getSamochod().getId()));
            istniejący.setSamochod(samochod);
        }

        return zakupRepository.save(istniejący);
    }

    // --- Usuwanie zakupu ---
    public void deleteZakup(Long id) {
        if (!zakupRepository.existsById(id)) {
            throw new NoSuchElementException("Nie znaleziono zakupu o ID: " + id);
        }
        zakupRepository.deleteById(id);
    }
}
