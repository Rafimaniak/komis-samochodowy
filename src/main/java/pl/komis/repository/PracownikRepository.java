package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.komis.model.Pracownik;

import java.util.List;

public interface PracownikRepository extends JpaRepository<Pracownik, Long> {

    List<Pracownik> findByStanowiskoIgnoreCase(String stanowisko);

    Pracownik findByEmail(String email);
}
