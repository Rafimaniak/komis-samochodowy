package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.komis.model.Samochod;
import pl.komis.repository.SamochodRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SamochodService {

    private final SamochodRepository samochodRepository;

    @Transactional(readOnly = true)
    public List<Samochod> findAll() {
        return samochodRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Samochod> findById(Long id) {
        return samochodRepository.findById(id);
    }

    @Transactional
    public Samochod save(Samochod samochod) {
        return samochodRepository.save(samochod);
    }

    @Transactional
    public void delete(Long id) {
        samochodRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByMarka(String marka) {
        return samochodRepository.findByMarkaIgnoreCase(marka);
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByModel(String model) {
        return samochodRepository.findByModelIgnoreCase(model);
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByStatus(String status) {
        return samochodRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByCenaBetween(BigDecimal min, BigDecimal max) {
        return samochodRepository.findByCenaBetween(min, max);
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByCenaBetween(double min, double max) {
        BigDecimal minCena = BigDecimal.valueOf(min);
        BigDecimal maxCena = BigDecimal.valueOf(max);
        return samochodRepository.findByCenaBetween(minCena, maxCena);
    }

    // Dodatkowe przydatne metody:

    @Transactional(readOnly = true)
    public long count() {
        return samochodRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return samochodRepository.findByStatus(status).size();
    }

    @Transactional(readOnly = true)
    public List<Samochod> findAvailableCars() {
        return samochodRepository.findByStatus("DOSTEPNY");
    }

    @Transactional(readOnly = true)
    public List<Samochod> findSoldCars() {
        return samochodRepository.findByStatus("SPRZEDANY");
    }

    @Transactional(readOnly = true)
    public List<Samochod> findReservedCars() {
        return samochodRepository.findByStatus("ZAREZERWOWANY");
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return samochodRepository.existsById(id);
    }

    @Transactional
    public Samochod updateStatus(Long id, String newStatus) {
        Samochod samochod = samochodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));
        samochod.setStatus(newStatus);
        return samochodRepository.save(samochod);
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByRokProdukcjiBetween(int minRok, int maxRok) {
        return samochodRepository.findAll().stream()
                .filter(s -> s.getRokProdukcji() >= minRok && s.getRokProdukcji() <= maxRok)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByPrzebiegLessThan(int maxPrzebieg) {
        return samochodRepository.findAll().stream()
                .filter(s -> s.getPrzebieg() <= maxPrzebieg)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Samochod> findByRodzajPaliwa(String rodzajPaliwa) {
        return samochodRepository.findAll().stream()
                .filter(s -> rodzajPaliwa.equalsIgnoreCase(s.getRodzajPaliwa()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Samochod> findBySkrzyniaBiegow(String skrzyniaBiegow) {
        return samochodRepository.findAll().stream()
                .filter(s -> skrzyniaBiegow.equalsIgnoreCase(s.getSkrzyniaBiegow()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> findAllMarki() {
        return samochodRepository.findAll().stream()
                .map(Samochod::getMarka)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> findAllModele() {
        return samochodRepository.findAll().stream()
                .map(Samochod::getModel)
                .distinct()
                .collect(Collectors.toList());
    }
}