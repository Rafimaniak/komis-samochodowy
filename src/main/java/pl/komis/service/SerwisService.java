package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.komis.model.Serwis;
import pl.komis.repository.SerwisRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SerwisService {

    private final SerwisRepository serwisRepository;

    @Transactional(readOnly = true)
    public List<Serwis> findAll() {
        return serwisRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Serwis> findById(Long id) {
        return serwisRepository.findById(id);
    }

    @Transactional
    public Serwis save(Serwis serwis) {
        System.out.println("=== ZAPIS SERWISU ===");
        System.out.println("ID: " + serwis.getId());
        System.out.println("Samochód ID: " + (serwis.getSamochod() != null ? serwis.getSamochod().getId() : "NULL"));
        System.out.println("Pracownik ID: " + (serwis.getPracownik() != null ? serwis.getPracownik().getId() : "NULL"));
        System.out.println("Koszt: " + serwis.getKoszt());
        System.out.println("Data: " + serwis.getDataSerwisu());

        if (serwis.getId() == null) {
            Long newId = serwisRepository.createService(
                    serwis.getSamochod().getId(),
                    serwis.getPracownik().getId(),
                    serwis.getOpisUslugi(),
                    serwis.getKoszt(),
                    serwis.getDataSerwisu()
            );
            return serwisRepository.findById(newId)
                    .orElseThrow(() -> new RuntimeException("Błąd podczas tworzenia serwisu"));
        } else {
            serwisRepository.updateService(
                    serwis.getId(),
                    serwis.getSamochod().getId(),
                    serwis.getPracownik().getId(),
                    serwis.getOpisUslugi(),
                    serwis.getKoszt(),
                    serwis.getDataSerwisu()
            );
            return serwis;
        }
    }

    @Transactional
    public void delete(Long id) {
        serwisRepository.deleteService(id);
    }

    @Transactional
    public Long reserveService(Long idSamochodu, Long idPracownika, String opisUslugi,
                               BigDecimal szacowanyKoszt, LocalDate dataSerwisu) {
        System.out.println("=== REZERWACJA SERWISU ===");
        System.out.println("Szacowany koszt: " + szacowanyKoszt);

        // Sprawdź czy szacowany koszt jest poprawny
        if (szacowanyKoszt == null || szacowanyKoszt.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Szacowany koszt musi być liczbą dodatnią");
        }

        return serwisRepository.reserveService(idSamochodu, idPracownika, opisUslugi, szacowanyKoszt, dataSerwisu);
    }

    @Transactional
    public void completeService(Long serviceId, BigDecimal rzeczywistyKoszt, String dodatkoweUwagi) {
        System.out.println("=== KOŃCZENIE SERWISU ===");
        System.out.println("Rzeczywisty koszt: " + rzeczywistyKoszt);

        Serwis serwis = serwisRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serwis nie znaleziony"));

        String nowyOpis = serwis.getOpisUslugi();
        if (dodatkoweUwagi != null && !dodatkoweUwagi.trim().isEmpty()) {
            nowyOpis += "\n\nUwagi po wykonaniu: " + dodatkoweUwagi;
        }

        serwisRepository.completeService(serviceId, rzeczywistyKoszt, nowyOpis);
    }

    @Transactional
    public void cancelService(Long serviceId) {
        serwisRepository.cancelService(serviceId);
    }

    @Transactional(readOnly = true)
    public List<Serwis> findBySamochodId(Long samochodId) {
        return serwisRepository.findBySamochodId(samochodId);
    }

    @Transactional(readOnly = true)
    public List<Serwis> findByPracownikId(Long pracownikId) {
        return serwisRepository.findByPracownikId(pracownikId);
    }

    @Transactional(readOnly = true)
    public List<Serwis> findByDateRange(LocalDate od, LocalDate do_) {
        return serwisRepository.findByDataSerwisuBetween(od, do_);
    }

    @Transactional(readOnly = true)
    public long countReservedServices() {
        return serwisRepository.countReservedServices();
    }

    @Transactional(readOnly = true)
    public long countCompletedServices() {
        return serwisRepository.countCompletedServices();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalServiceCost() {
        return serwisRepository.getTotalServiceCost();
    }

    @Transactional(readOnly = true)
    public List<Serwis> findReservedServices() {
        return serwisRepository.findAll().stream()
                .filter(s -> s.getKoszt() == null)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Serwis> findCompletedServices() {
        return serwisRepository.findAll().stream()
                .filter(s -> s.getKoszt() != null)
                .toList();
    }
}