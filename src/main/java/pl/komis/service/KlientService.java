package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Klient;
import pl.komis.repository.KlientRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KlientService {

    private final KlientRepository klientRepository;
    private final UserService userService; // DODAJ TEN IMPORT

    public List<Klient> findAll() {
        return klientRepository.findAll();
    }

    public Optional<Klient> findById(Long id) {
        return klientRepository.findById(id);
    }

    public Optional<Klient> findByEmail(String email) {
        return klientRepository.findByEmail(email);
    }

    public Klient save(Klient klient) {
        return klientRepository.save(klient);
    }

    public void delete(Long id) {
        klientRepository.deleteById(id);
    }

    // DODAJ METODĘ DO POBRANIA KLIENTA NA PODSTAWIE USERNAME
    public Klient getKlientByUsername(String username) {
        return userService.findByUsername(username)
                .map(user -> user.getKlient())
                .orElse(null);
    }

    // METODA DO AKTUALIZACJI RABATU (opcjonalnie)
    public void aktualizujRabat(Long klientId) {
        klientRepository.findById(klientId).ifPresent(klient -> {
            // Możesz dodać logikę aktualizacji rabatu tutaj
            // lub zostawić puste, bo trigger w bazie robi to automatycznie
        });
    }

    // DODAJ METODĘ DO POBRANIA LICZBY ZAKUPÓW
    public Integer getLiczbaZakupow(Long klientId) {
        return klientRepository.findById(klientId)
                .map(Klient::getLiczbaZakupow)
                .orElse(0);
    }
}