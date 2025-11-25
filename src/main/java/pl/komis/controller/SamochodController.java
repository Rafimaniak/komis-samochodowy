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

    // Widok listy samochodów z wyszukiwarką
    @GetMapping
    public String listaSamochodow(
            @RequestParam(required = false) String marka,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minRok,
            @RequestParam(required = false) Integer maxRok,
            @RequestParam(required = false) Integer minPrzebieg,
            @RequestParam(required = false) Integer maxPrzebieg,
            @RequestParam(required = false) BigDecimal minCena,
            @RequestParam(required = false) BigDecimal maxCena,
            Model modelAttr) {

        List<Samochod> samochody;

        // Sprawdź czy są parametry wyszukiwania (uwzględniając puste stringi)
        boolean hasSearchParams = (marka != null && !marka.trim().isEmpty()) ||
                (model != null && !model.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty()) ||
                minRok != null || maxRok != null ||
                minPrzebieg != null || maxPrzebieg != null ||
                minCena != null || maxCena != null;

        if (hasSearchParams) {
            // Użyj zaawansowanego wyszukiwania
            SamochodService.SearchCriteria criteria = new SamochodService.SearchCriteria();
            criteria.setMarka(marka);
            criteria.setModel(model);
            criteria.setStatus(status);
            criteria.setMinRok(minRok);
            criteria.setMaxRok(maxRok);
            criteria.setMinPrzebieg(minPrzebieg);
            criteria.setMaxPrzebieg(maxPrzebieg);
            criteria.setMinCena(minCena);
            criteria.setMaxCena(maxCena);

            samochody = samochodService.searchCars(criteria);

            // DEBUG: Sprawdź wyniki wyszukiwania
            System.out.println("DEBUG: Kryteria wyszukiwania: " + criteria);
            System.out.println("DEBUG: Znaleziono samochodów: " + samochody.size());
        } else {
            // Pokaż wszystkie samochody
            samochody = samochodService.findAll();
        }

        modelAttr.addAttribute("samochody", samochody);
        modelAttr.addAttribute("marki", samochodService.findAllMarki());
        modelAttr.addAttribute("tytul", "Lista Samochodów");
        modelAttr.addAttribute("hasSearchParams", hasSearchParams);

        // Przekaż parametry wyszukiwania z powrotem do formularza
        modelAttr.addAttribute("searchMarka", marka);
        modelAttr.addAttribute("searchModel", model);
        modelAttr.addAttribute("searchStatus", status);
        modelAttr.addAttribute("searchMinRok", minRok);
        modelAttr.addAttribute("searchMaxRok", maxRok);
        modelAttr.addAttribute("searchMinPrzebieg", minPrzebieg);
        modelAttr.addAttribute("searchMaxPrzebieg", maxPrzebieg);
        modelAttr.addAttribute("searchMinCena", minCena);
        modelAttr.addAttribute("searchMaxCena", maxCena);

        return "samochody/lista";
    }

    // Pozostałe metody pozostają bez zmian...
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