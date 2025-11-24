package pl.komis.service;

import org.springframework.stereotype.Service;
import pl.komis.model.Pracownik;
import pl.komis.repository.PracownikRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PracownikService {

    private final PracownikRepository pracownikRepository;

    public PracownikService(PracownikRepository pracownikRepository) {
        this.pracownikRepository = pracownikRepository;
    }

    // Pobierz wszystkich pracowników
    public List<Pracownik> getAllPracownicy() {
        return pracownikRepository.findAll();
    }

    // Pobierz pracownika po ID
    public Optional<Pracownik> getPracownikById(Long id) {
        return pracownikRepository.findById(id);
    }

    // Zapisz lub zaktualizuj pracownika
    public Pracownik savePracownik(Pracownik pracownik) {
        return pracownikRepository.save(pracownik);
    }

    // Usuń pracownika po ID
    public void deletePracownik(Long id) {
        pracownikRepository.deleteById(id);
    }


}

