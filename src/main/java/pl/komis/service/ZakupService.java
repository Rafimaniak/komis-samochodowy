package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Zakup;
import pl.komis.repository.ZakupRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ZakupService {

    private final ZakupRepository zakupRepository;

    public List<Zakup> findAll() {
        return zakupRepository.findAll();
    }

    public Zakup getById(Long id) {
        return zakupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Zakup o ID " + id + " nie istnieje"));
    }

    public Zakup save(Zakup zakup) {
        return zakupRepository.save(zakup);
    }

    public void remove(Long id) {
        zakupRepository.deleteById(id);
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
}
