package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.komis.model.Klient;

import java.util.Optional;

public interface KlientRepository extends JpaRepository<Klient, Long> {

    Optional<Klient> findByEmail(String email);
}
