package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.komis.model.Promocja;

import java.time.LocalDate;
import java.util.List;

public interface PromocjaRepository extends JpaRepository<Promocja, Long> {

    List<Promocja> findByDataRozpoczeciaBeforeAndDataZakonczeniaAfter(LocalDate from, LocalDate to);
}
