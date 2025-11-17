package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.komis.model.Samochod;
import pl.komis.service.SamochodService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/samochody")
@RequiredArgsConstructor
public class SamochodController {

    private final SamochodService samochodService;

    @GetMapping
    public List<Samochod> getAll() {
        return samochodService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Samochod> getById(@PathVariable Long id) {
        return samochodService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/marka/{marka}")
    public List<Samochod> getByMarka(@PathVariable String marka) {
        return samochodService.findByMarka(marka);
    }

    @GetMapping("/model/{model}")
    public List<Samochod> getByModel(@PathVariable String model) {
        return samochodService.findByModel(model);
    }

    @GetMapping("/status/{status}")
    public List<Samochod> getByStatus(@PathVariable String status) {
        return samochodService.findByStatus(status);
    }

    @GetMapping("/cena")
    public List<Samochod> getByCenaBetween(@RequestParam BigDecimal min,
                                           @RequestParam BigDecimal max) {
        return samochodService.findByCenaBetween(min, max);
    }

    @PostMapping
    public Samochod create(@RequestBody Samochod samochod) {
        return samochodService.save(samochod);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Samochod> update(@PathVariable Long id,
                                           @RequestBody Samochod samochod) {
        if (!samochodService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        samochod.setId(id);
        return ResponseEntity.ok(samochodService.save(samochod));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!samochodService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        samochodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}