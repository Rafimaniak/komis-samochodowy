package pl.komis.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.komis.model.Samochod;
import pl.komis.repository.SamochodRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SamochodService {

    private final SamochodRepository samochodRepository;

    // ==================== PODSTAWOWE OPERACJE CRUD ====================

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
        // Ustaw domyślne wartości jeżeli potrzebne
        if (samochod.getDataDodania() == null) {
            samochod.setDataDodania(LocalDate.now());
        }
        if (samochod.getRodzajPaliwa() == null) {
            samochod.setRodzajPaliwa("Benzyna");
        }
        if (samochod.getSkrzyniaBiegow() == null) {
            samochod.setSkrzyniaBiegow("Manualna");
        }
        if (samochod.getPojemnoscSilnika() == null) {
            samochod.setPojemnoscSilnika(2.0);
        }
        if (samochod.getStatus() == null) {
            samochod.setStatus("DOSTEPNY");
        }

        String zdjecieUrl = samochod.getZdjecieUrl();
        if (zdjecieUrl == null) {
            zdjecieUrl = "";
        }

        if (samochod.getId() == null) {
            // Tworzenie nowego samochodu przez procedurę
            Long newCarId = samochodRepository.createCar(
                    samochod.getMarka(),
                    samochod.getModel(),
                    samochod.getRokProdukcji(),
                    samochod.getPrzebieg(),
                    samochod.getPojemnoscSilnika(),
                    samochod.getRodzajPaliwa(),
                    samochod.getSkrzyniaBiegow(),
                    samochod.getKolor(),
                    samochod.getCena(),
                    samochod.getStatus(),
                    zdjecieUrl
            );

            return samochodRepository.findById(newCarId)
                    .orElseThrow(() -> new RuntimeException("Błąd podczas tworzenia samochodu"));
        } else {
            // ZAPISZ WSZYSTKIE POLA SAMOCHODU
            // Użyj standardowego save() zamiast procedury, aby zapisać wszystkie pola
            return samochodRepository.save(samochod);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!samochodRepository.existsById(id)) {
            throw new RuntimeException("Samochód nie znaleziony");
        }
        // Użycie procedury usuwania
        samochodRepository.deleteCar(id);
    }

    // ==================== ZAAWANSOWANE WYSZUKIWANIE ====================

    @Transactional(readOnly = true)
    public List<Samochod> searchCars(SearchCriteria criteria) {
        // Upewnij się, że puste stringi są traktowane jako null
        String marka = (criteria.getMarka() != null && !criteria.getMarka().trim().isEmpty()) ? criteria.getMarka() : null;
        String model = (criteria.getModel() != null && !criteria.getModel().trim().isEmpty()) ? criteria.getModel() : null;
        String status = (criteria.getStatus() != null && !criteria.getStatus().trim().isEmpty()) ? criteria.getStatus() : null;
        String rodzajPaliwa = (criteria.getRodzajPaliwa() != null && !criteria.getRodzajPaliwa().trim().isEmpty()) ? criteria.getRodzajPaliwa() : null;

        return samochodRepository.searchCars(
                marka,
                model,
                criteria.getMinRok(),
                criteria.getMaxRok(),
                criteria.getMinPrzebieg(),
                criteria.getMaxPrzebieg(),
                criteria.getMinCena(),
                criteria.getMaxCena(),
                rodzajPaliwa,
                status
        );
    }

    @Transactional(readOnly = true)
    public List<Samochod> searchCarsSimple(String marka, String model, String status) {
        // Upewnij się, że puste stringi są traktowane jako null
        String searchMarka = (marka != null && !marka.trim().isEmpty()) ? marka : null;
        String searchModel = (model != null && !model.trim().isEmpty()) ? model : null;
        String searchStatus = (status != null && !status.trim().isEmpty()) ? status : null;

        return samochodRepository.searchCarsSimple(searchMarka, searchModel, searchStatus);
    }

    // ==================== OPERACJE BIZNESOWE ====================

    @Transactional
    public Map<String, Object> reserveCar(Long carId, Long userId) {
        try {
            return samochodRepository.reserveCar(carId, userId);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas rezerwacji samochodu: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> sellCar(Long carId, Long userId, Long employeeId, BigDecimal finalPrice) {
        try {
            return samochodRepository.sellCar(carId, userId, employeeId, finalPrice);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas sprzedaży samochodu: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> cancelReservation(Long carId) {
        try {
            return samochodRepository.cancelReservation(carId);
        } catch (Exception e) {
            throw new RuntimeException("Błąd podczas anulowania rezerwacji: " + e.getMessage());
        }
    }

    // ==================== METODY POMOCNICZE ====================

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
    public long count() {
        return samochodRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByStatus(String status) {
        return samochodRepository.countByStatus(status);
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

        samochodRepository.updateCar(
                samochod.getId(),
                samochod.getMarka(),
                samochod.getModel(),
                samochod.getRokProdukcji(),
                samochod.getPrzebieg(),
                samochod.getPojemnoscSilnika(),
                samochod.getRodzajPaliwa(),
                samochod.getSkrzyniaBiegow(),
                samochod.getKolor(),
                samochod.getCena(),
                newStatus,
                samochod.getZdjecieUrl()
        );

        return samochodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Błąd podczas aktualizacji statusu samochodu"));
    }

    @Transactional(readOnly = true)
    public List<String> findAllMarki() {
        return samochodRepository.findAllMarkiDistinct();
    }

    // ==================== NOWE METODY DLA STATYSTYK ====================

    @Transactional(readOnly = true)
    public Map<String, Object> getCarStatistics() {
        return samochodRepository.getCarStatistics();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPopularBrands(Integer limit) {
        return samochodRepository.getPopularBrands(limit);
    }

    // ==================== KLASY POMOCNICZE ====================

    @Data
    public static class SearchCriteria {
        private String marka;
        private String model;
        private Integer minRok;
        private Integer maxRok;
        private Integer minPrzebieg;
        private Integer maxPrzebieg;
        private BigDecimal minCena;
        private BigDecimal maxCena;
        private String rodzajPaliwa;
        private String status;
    }
}