package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.komis.model.Serwis;
import pl.komis.model.Samochod;
import pl.komis.model.Pracownik;
import pl.komis.service.SerwisService;
import pl.komis.service.SamochodService;
import pl.komis.service.PracownikService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/serwis")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class SerwisController {

    private final SerwisService serwisService;
    private final SamochodService samochodService;
    private final PracownikService pracownikService;

    @GetMapping
    public String listaSerwisow(Model model) {
        model.addAttribute("serwisy", serwisService.findAll());
        model.addAttribute("tytul", "Lista Serwisów");
        model.addAttribute("zarezerwowane", serwisService.countReservedServices());
        model.addAttribute("zakonczone", serwisService.countCompletedServices());
        model.addAttribute("lacznyKoszt", serwisService.getTotalServiceCost());
        return "serwis/lista";
    }

    @GetMapping("/dodaj")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może dodawać
    public String showAddForm(Model model) {
        Serwis serwis = new Serwis();
        serwis.setDataSerwisu(LocalDate.now());

        model.addAttribute("serwis", serwis);
        model.addAttribute("samochody", samochodService.findAll());
        model.addAttribute("pracownicy", pracownikService.findAll());
        model.addAttribute("tytul", "Dodaj Serwis");
        return "serwis/form";
    }

    @PostMapping("/dodaj")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może dodawać
    public String addSerwis(@RequestParam("samochod") Long samochodId,
                            @RequestParam("pracownik") Long pracownikId,
                            @RequestParam String opisUslugi,
                            @RequestParam(required = false) BigDecimal koszt,
                            @RequestParam LocalDate dataSerwisu,
                            RedirectAttributes redirectAttributes) {
        try {
            Samochod samochod = samochodService.findById(samochodId)
                    .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

            Pracownik pracownik = pracownikService.findById(pracownikId)
                    .orElseThrow(() -> new RuntimeException("Pracownik nie znaleziony"));

            Serwis serwis = new Serwis();
            serwis.setSamochod(samochod);
            serwis.setPracownik(pracownik);
            serwis.setOpisUslugi(opisUslugi);
            serwis.setKoszt(koszt);
            serwis.setDataSerwisu(dataSerwisu);

            serwisService.save(serwis);

            redirectAttributes.addFlashAttribute("successMessage", "Serwis został dodany pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas dodawania serwisu: " + e.getMessage());
        }
        return "redirect:/serwis";
    }

    @GetMapping("/edytuj/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może edytować
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            System.out.println("=== DEBUG EDYCJA GET: ID=" + id + " ===");

            Serwis serwis = serwisService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Serwis nie znaleziony"));

            System.out.println("=== DEBUG: Znaleziono serwis ===");
            System.out.println("ID: " + serwis.getId());
            System.out.println("Samochód ID: " + (serwis.getSamochod() != null ? serwis.getSamochod().getId() : "NULL"));
            System.out.println("Pracownik ID: " + (serwis.getPracownik() != null ? serwis.getPracownik().getId() : "NULL"));
            System.out.println("Koszt: " + serwis.getKoszt());
            System.out.println("Status: " + serwis.getStatus());

            model.addAttribute("serwis", serwis);
            model.addAttribute("samochody", samochodService.findAll());
            model.addAttribute("pracownicy", pracownikService.findAll());
            model.addAttribute("tytul", "Edytuj Serwis");

            return "serwis/form";

        } catch (Exception e) {
            System.err.println("=== BŁĄD EDYCJI GET: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Błąd podczas ładowania formularza edycji: " + e.getMessage());
        }
    }

    @PostMapping("/edytuj/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może edytować
    public String updateSerwis(@PathVariable Long id,
                               @RequestParam("samochod") Long samochodId,
                               @RequestParam("pracownik") Long pracownikId,
                               @RequestParam String opisUslugi,
                               @RequestParam(required = false) BigDecimal koszt,
                               @RequestParam LocalDate dataSerwisu,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== DEBUG EDYCJA POST ===");
            System.out.println("ID: " + id);
            System.out.println("Samochód ID: " + samochodId);
            System.out.println("Pracownik ID: " + pracownikId);
            System.out.println("Koszt: " + koszt);
            System.out.println("Data: " + dataSerwisu);

            Serwis existingSerwis = serwisService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Serwis nie znaleziony"));

            Samochod samochod = samochodService.findById(samochodId)
                    .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

            Pracownik pracownik = pracownikService.findById(pracownikId)
                    .orElseThrow(() -> new RuntimeException("Pracownik nie znaleziony"));

            existingSerwis.setSamochod(samochod);
            existingSerwis.setPracownik(pracownik);
            existingSerwis.setOpisUslugi(opisUslugi);
            existingSerwis.setKoszt(koszt);
            existingSerwis.setDataSerwisu(dataSerwisu);

            serwisService.save(existingSerwis);

            redirectAttributes.addFlashAttribute("successMessage", "Serwis został zaktualizowany pomyślnie");
            return "redirect:/serwis";

        } catch (Exception e) {
            System.err.println("=== BŁĄD EDYCJI POST: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas edycji serwisu: " + e.getMessage());
            return "redirect:/serwis/edytuj/" + id;
        }
    }

    @GetMapping("/rezerwuj")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może rezerwować
    public String showReserveForm(Model model) {
        model.addAttribute("samochody", samochodService.findAll());
        model.addAttribute("pracownicy", pracownikService.findAll());
        model.addAttribute("tytul", "Zarezerwuj Serwis");
        return "serwis/rezerwacja";
    }

    @PostMapping("/rezerwuj")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może rezerwować
    public String reserveService(
            @RequestParam Long samochod,
            @RequestParam Long pracownik,
            @RequestParam String opisUslugi,
            @RequestParam String szacowanyKoszt,
            @RequestParam LocalDate dataSerwisu,
            RedirectAttributes redirectAttributes) {
        try {
            BigDecimal koszt;
            try {
                koszt = new BigDecimal(szacowanyKoszt.replace(",", "."));
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Nieprawidłowy format kwoty");
                return "redirect:/serwis/rezerwuj";
            }

            if (koszt.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Szacowany koszt musi być większy od zera");
                return "redirect:/serwis/rezerwuj";
            }

            Long serviceId = serwisService.reserveService(samochod, pracownik, opisUslugi, koszt, dataSerwisu);
            redirectAttributes.addFlashAttribute("successMessage", "Serwis został zarezerwowany pomyślnie (ID: " + serviceId + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas rezerwacji serwisu: " + e.getMessage());
        }
        return "redirect:/serwis";
    }

    @GetMapping("/zakoncz/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może kończyć
    public String showCompleteForm(@PathVariable Long id, Model model) {
        Serwis serwis = serwisService.findById(id)
                .orElseThrow(() -> new RuntimeException("Serwis nie znaleziony"));
        model.addAttribute("serwis", serwis);
        model.addAttribute("tytul", "Zakończ Serwis");
        return "serwis/zakonczenie";
    }

    @PostMapping("/zakoncz/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może kończyć
    public String completeService(
            @PathVariable Long id,
            @RequestParam BigDecimal rzeczywistyKoszt,
            @RequestParam(required = false) String dodatkoweUwagi,
            RedirectAttributes redirectAttributes) {
        try {
            serwisService.completeService(id, rzeczywistyKoszt, dodatkoweUwagi);
            redirectAttributes.addFlashAttribute("successMessage", "Serwis został zakończony pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas kończenia serwisu: " + e.getMessage());
        }
        return "redirect:/serwis";
    }

    @PostMapping("/anuluj/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może anulować
    public String cancelService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serwisService.cancelService(id);
            redirectAttributes.addFlashAttribute("successMessage", "Serwis został anulowany pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas anulowania serwisu: " + e.getMessage());
        }
        return "redirect:/serwis";
    }

    @PostMapping("/usun/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Tylko ADMIN może usuwać
    public String deleteSerwis(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serwisService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Serwis został usunięty pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania serwisu: " + e.getMessage());
        }
        return "redirect:/serwis";
    }

    @GetMapping("/zarezerwowane")
    public String listaZarezerwowanych(Model model) {
        model.addAttribute("serwisy", serwisService.findReservedServices());
        model.addAttribute("tytul", "Zarezerwowane Serwisy");
        return "serwis/lista";
    }

    @GetMapping("/zakonczone")
    public String listaZakonczonych(Model model) {
        model.addAttribute("serwisy", serwisService.findCompletedServices());
        model.addAttribute("tytul", "Zakończone Serwisy");
        return "serwis/lista";
    }
}