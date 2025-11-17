package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Klient;
import pl.komis.repository.KlientRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KlientService {

    private final KlientRepository klientRepository;

    public List<Klient> findAll() {
        return klientRepository.findAll();
    }

    public Klient getById(Long id) {
        return klientRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Klient o id " + id + " nie istnieje."));
    }

    public Klient save(Klient klient) {
        return klientRepository.save(klient);
    }

    public void remove(Long id) {
        if (!klientRepository.existsById(id)) {
            throw new NoSuchElementException("Nie można usunąć — klient o id " + id + " nie istnieje.");
        }
        klientRepository.deleteById(id);
    }

    public Optional<Klient> findByEmail(String email) {
        return klientRepository.findByEmail(email);
    }
}
