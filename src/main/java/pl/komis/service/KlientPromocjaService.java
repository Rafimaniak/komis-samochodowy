package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.komis.model.Klient;
import pl.komis.model.KlientPromocja;
import pl.komis.model.Promocja;
import pl.komis.repository.KlientPromocjaRepository;
import pl.komis.repository.KlientRepository;
import pl.komis.repository.PromocjaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KlientPromocjaService {

    private final KlientPromocjaRepository klientPromocjaRepository;
    private final KlientRepository klientRepository;
    private final PromocjaRepository promocjaRepository;

    public List<KlientPromocja> findAll() {
        return klientPromocjaRepository.findAll();
    }

    public KlientPromocja findById(Long id) {
        return klientPromocjaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przypisania promocji o id=" + id));
    }

    public List<KlientPromocja> findByKlientId(Long klientId) {
        return klientPromocjaRepository.findByKlientId(klientId);
    }

    public List<KlientPromocja> findByPromocjaId(Long promocjaId) {
        return klientPromocjaRepository.findByPromocjaId(promocjaId);
    }

    // ============================
    //    NAJWAŻNIEJSZE METODY
    // ============================

    public KlientPromocja assignPromotion(Long idKlienta, Long idPromocji) {

        Klient klient = klientRepository.findById(idKlienta)
                .orElseThrow(() -> new RuntimeException("Klient nie istnieje: " + idKlienta));

        Promocja promocja = promocjaRepository.findById(idPromocji)
                .orElseThrow(() -> new RuntimeException("Promocja nie istnieje: " + idPromocji));

        // tworzymy przypisanie
        KlientPromocja kp = new KlientPromocja();
        kp.setKlient(klient);
        kp.setPromocja(promocja);

        return klientPromocjaRepository.save(kp);
    }

    public void remove(Long id) {
        if (!klientPromocjaRepository.existsById(id)) {
            throw new RuntimeException("Nie znaleziono przypisania do usunięcia: " + id);
        }
        klientPromocjaRepository.deleteById(id);
    }
}