package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Pracownik;
import pl.komis.repository.PracownikRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PracownikService {

    private final PracownikRepository pracownikRepository;

    // DODAJ tę metodę jeśli jej nie masz:
    public Optional<Pracownik> findById(Long id) {
        return pracownikRepository.findById(id);
    }

    // Pozostałe metody...
    public List<Pracownik> findAll() {
        return pracownikRepository.findAll();
    }

    public Pracownik getById(Long id) {
        return pracownikRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pracownik o ID " + id + " nie istnieje"));
    }

    public Pracownik save(Pracownik pracownik) {
        return pracownikRepository.save(pracownik);
    }

    public void remove(Long id) {
        pracownikRepository.deleteById(id);
    }
}