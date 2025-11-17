package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.komis.model.KlientPromocja;
import pl.komis.service.KlientPromocjaService;

import java.util.List;

@RestController
@RequestMapping("/api/klient-promocje")
@RequiredArgsConstructor
public class KlientPromocjaController {

    private final KlientPromocjaService klientPromocjaService;

    @GetMapping
    public List<KlientPromocja> getAll() {
        return klientPromocjaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<KlientPromocja> getById(@PathVariable Long id) {
        return ResponseEntity.ok(klientPromocjaService.findById(id));
    }

    @PostMapping("/assign/{idKlienta}/{idPromocji}")
    public ResponseEntity<KlientPromocja> assign(@PathVariable Long idKlienta,
                                                 @PathVariable Long idPromocji) {
        return ResponseEntity.ok(
                klientPromocjaService.assignPromotion(idKlienta, idPromocji)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        klientPromocjaService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-klient/{idKlienta}")
    public List<KlientPromocja> byKlient(@PathVariable Long idKlienta) {
        return klientPromocjaService.findByKlientId(idKlienta);
    }

    @GetMapping("/by-promocja/{idPromocji}")
    public List<KlientPromocja> byPromocja(@PathVariable Long idPromocji) {
        return klientPromocjaService.findByPromocjaId(idPromocji);
    }
}
