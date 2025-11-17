package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Promocja;
import pl.komis.repository.PromocjaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PromocjaService {

    private final PromocjaRepository promocjaRepository;

    public List<Promocja> findAll() {
        return promocjaRepository.findAll();
    }

    public Promocja getById(Long id) {
        return promocjaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Promocja o id " + id + " nie istnieje."));
    }

    public Promocja save(Promocja promocja) {
        return promocjaRepository.save(promocja);
    }

    public Promocja update(Long id, Promocja data) {
        Promocja p = getById(id);

        p.setNazwa(data.getNazwa());
        p.setRodzaj(data.getRodzaj());
        p.setWartosc(data.getWartosc());
        p.setOpis(data.getOpis());
        p.setDataRozpoczecia(data.getDataRozpoczecia());
        p.setDataZakonczenia(data.getDataZakonczenia());
        p.setAktywna(data.getAktywna());

        return promocjaRepository.save(p);
    }

    public void remove(Long id) {
        if (!promocjaRepository.existsById(id)) {
            throw new NoSuchElementException("Promocja o id " + id + " nie istnieje.");
        }
        promocjaRepository.deleteById(id);
    }

    public List<Promocja> findActive() {
        LocalDate today = LocalDate.now();
        return promocjaRepository.findByDataRozpoczeciaBeforeAndDataZakonczeniaAfter(today, today);
    }
}
