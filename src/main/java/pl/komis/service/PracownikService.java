package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Pracownik;
import pl.komis.repository.PracownikRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PracownikService {

    private final PracownikRepository pracownikRepository;

    public List<Pracownik> findAll() {
        return pracownikRepository.findAll();
    }

    public Pracownik getById(Long id) {
        return pracownikRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pracownik o ID " + id + " nie istnieje"));
    }

    public Pracownik save(Pracownik pracownik) {
        return pracownikRepository.save(pracownik);
    }

    public void remove(Long id) {
        pracownikRepository.deleteById(id);
    }
}


