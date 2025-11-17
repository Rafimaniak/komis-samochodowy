package pl.komis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.komis.model.Samochod;
import pl.komis.repository.SamochodRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class SamochodService {

    private final SamochodRepository samochodRepository;

    public SamochodService(SamochodRepository samochodRepository) {
        this.samochodRepository = samochodRepository;
    }

    public List<Samochod> findAll() {
        return samochodRepository.findAll();
    }

    public Optional<Samochod> findById(Long id) {
        return samochodRepository.findById(id);
    }

    public Samochod save(Samochod samochod) {
        return samochodRepository.save(samochod);
    }

    public void delete(Long id) {
        samochodRepository.deleteById(id);
    }

    public List<Samochod> findByMarka(String marka) {
        return samochodRepository.findByMarkaIgnoreCase(marka);
    }

    public List<Samochod> findByModel(String model) {
        return samochodRepository.findByModelIgnoreCase(model);
    }

    public List<Samochod> findByStatus(String status) {
        return samochodRepository.findByStatus(status);
    }

    public List<Samochod> findByCenaBetween(BigDecimal min, BigDecimal max) {
        return samochodRepository.findByCenaBetween(min, max);
    }

    public List<Samochod> findByCenaBetween(double min, double max) {
        BigDecimal minCena = BigDecimal.valueOf(min);
        BigDecimal maxCena = BigDecimal.valueOf(max);
        return samochodRepository.findByCenaBetween(minCena, maxCena);
    }
}