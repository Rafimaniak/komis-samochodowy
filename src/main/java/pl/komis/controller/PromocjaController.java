package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.komis.model.Promocja;
import pl.komis.service.PromocjaService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/promocje")
@RequiredArgsConstructor
public class PromocjaController {

    private final PromocjaService promocjaService;

    @GetMapping
    public List<Promocja> getAll() {
        return promocjaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocja> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(promocjaService.getById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Promocja> create(@RequestBody Promocja p) {
        Promocja created = promocjaService.save(p);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promocja> update(@PathVariable Long id, @RequestBody Promocja p) {
        try {
            Promocja updated = promocjaService.update(id, p);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            promocjaService.remove(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/aktywne")
    public List<Promocja> getActive() {
        return promocjaService.findActive();
    }
}
