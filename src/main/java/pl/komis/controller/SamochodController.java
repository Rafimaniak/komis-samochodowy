package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.komis.model.Samochod;
import pl.komis.service.SamochodService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/samochody")
@RequiredArgsConstructor
public class SamochodController {

    private final SamochodService samochodService;

    // Widok listy samochodów - dostępne dla wszystkich
    @GetMapping
    public String listaSamochodow(Model model) {
        List<Samochod> samochody = samochodService.findAll();
        model.addAttribute("samochody", samochody);
        model.addAttribute("tytul", "Lista Samochodów");
        return "samochody/lista";
    }

    // Strona szczegółów samochodu - dostępne dla wszystkich
    @GetMapping("/szczegoly")
    public String szczegolySamochodu(@RequestParam("id") Long id, Model model) {
        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));
        model.addAttribute("samochod", samochod);
        model.addAttribute("tytul", samochod.getMarka() + " " + samochod.getModel());
        return "samochody/szczegoly";
    }

    // Formularz dodawania nowego samochodu - tylko ADMIN
    @GetMapping("/nowy")
    @PreAuthorize("hasRole('ADMIN')")
    public String formNowySamochod(Model model) {
        model.addAttribute("samochod", new Samochod());
        model.addAttribute("tytul", "Dodaj Nowy Samochód");
        return "samochody/form";
    }

    // Zapisywanie nowego samochodu - tylko ADMIN
    @PostMapping("/zapisz")
    @PreAuthorize("hasRole('ADMIN')")
    public String zapiszSamochod(@ModelAttribute Samochod samochod) {
        // Ustaw domyślne wartości dla brakujących pól
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

        samochodService.save(samochod);
        return "redirect:/samochody";
    }

    // Formularz edycji samochodu - tylko ADMIN
    @GetMapping("/edytuj")
    @PreAuthorize("hasRole('ADMIN')")
    public String formEdycjaSamochodu(@RequestParam("id") Long id, Model model) {
        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));
        model.addAttribute("samochod", samochod);
        model.addAttribute("tytul", "Edytuj Samochód");
        return "samochody/form";
    }

    // Aktualizacja samochodu - tylko ADMIN
    @PostMapping("/edytuj")
    @PreAuthorize("hasRole('ADMIN')")
    public String aktualizujSamochod(@RequestParam("id") Long id, @ModelAttribute Samochod samochod) {
        samochod.setId(id);
        samochodService.save(samochod);
        return "redirect:/samochody";
    }

    // Usuwanie samochodu - tylko ADMIN
    @PostMapping("/usun")
    @PreAuthorize("hasRole('ADMIN')")
    public String usunSamochod(@RequestParam("id") Long id) {
        samochodService.delete(id);
        return "redirect:/samochody";
    }

    // Rezerwacja samochodu - dla zalogowanych użytkowników
    @PostMapping("/zarezerwuj")
    @PreAuthorize("isAuthenticated()")
    public String zarezerwujSamochod(@RequestParam("id") Long id) {
        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        if ("DOSTEPNY".equals(samochod.getStatus())) {
            samochod.setStatus("ZAREZERWOWANY");
            samochodService.save(samochod);
        }

        return "redirect:/samochody/szczegoly?id=" + id;
    }

    // Zakup samochodu - dla zalogowanych użytkowników
    @PostMapping("/kup")
    @PreAuthorize("isAuthenticated()")
    public String kupSamochod(@RequestParam("id") Long id) {
        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        if ("DOSTEPNY".equals(samochod.getStatus()) || "ZAREZERWOWANY".equals(samochod.getStatus())) {
            samochod.setStatus("SPRZEDANY");
            samochodService.save(samochod);
        }

        return "redirect:/samochody/szczegoly?id=" + id;
    }

    // Anulowanie rezerwacji - dla zalogowanych użytkowników
    @PostMapping("/anuluj-rezerwacje")
    @PreAuthorize("isAuthenticated()")
    public String anulujRezerwacje(@RequestParam("id") Long id) {
        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        if ("ZAREZERWOWANY".equals(samochod.getStatus())) {
            samochod.setStatus("DOSTEPNY");
            samochodService.save(samochod);
        }

        return "redirect:/samochody/szczegoly?id=" + id;
    }
}