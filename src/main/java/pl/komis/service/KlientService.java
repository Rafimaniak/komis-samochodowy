package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Klient;
import pl.komis.repository.KlientRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KlientService {

    private final KlientRepository klientRepository;

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

    public Integer getLiczbaZakupow(Long klientId) {
        return klientRepository.findById(klientId)
                .map(Klient::getLiczbaZakupow)
                .orElse(0);
    }

    public BigDecimal getSaldoPremii(Long klientId) {
        return klientRepository.findById(klientId)
                .map(Klient::getSaldoPremii)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getProcentPremii(Long klientId) {
        return klientRepository.findById(klientId)
                .map(Klient::getProcentPremii)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalWydane(Long klientId) {
        return klientRepository.findById(klientId)
                .map(Klient::getTotalWydane)
                .orElse(BigDecimal.ZERO);
    }
    public void naprawSaldo(Long klientId) {
        Klient klient = findById(klientId)
                .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));

        // Oblicz poprawną premię na podstawie historii zakupów
        BigDecimal sumaPremii = klientRepository.sumNaliczonaPremiaByKlientId(klientId);
        BigDecimal sumaWykorzystanegoSalda = klientRepository.sumWykorzystaneSaldoByKlientId(klientId);

        // Poprawne saldo = suma premii - suma wykorzystanego salda
        BigDecimal poprawneSaldo = sumaPremii.subtract(sumaWykorzystanegoSalda);

        klient.setSaldoPremii(poprawneSaldo);
        save(klient);
    }
}