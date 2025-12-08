package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.komis.model.User;
import pl.komis.model.Zakup;
import pl.komis.service.*;
import pl.komis.model.Klient;
import pl.komis.service.KlientService;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/zakupy")
@RequiredArgsConstructor
public class ZakupController {

    private final ZakupService zakupService;
    private final UserService userService;
    private final KlientService klientService;

    @GetMapping("/rabaty")
    @PreAuthorize("hasRole('ADMIN')")
    public String listaRabatow(Model model) {
        List<Klient> klienci = klientService.findAll();

        // Statystyki
        int totalZakupy = klienci.stream()
                .mapToInt(k -> k.getZakupy() != null ? k.getZakupy().size() : 0)
                .sum();

        double sredniRabat = klienci.stream()
                .mapToDouble(k -> k.getAktualnyRabat() != null ? k.getAktualnyRabat().doubleValue() : 0)
                .average()
                .orElse(0.0);

        BigDecimal totalZaoszczedzone = klienci.stream()
                .filter(k -> k.getZakupy() != null)
                .flatMap(k -> k.getZakupy().stream())
                .filter(z -> z.getCenaBazowa() != null && z.getCenaZakupu() != null)
                .map(z -> z.getCenaBazowa().subtract(z.getCenaZakupu()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("klienci", klienci);
        model.addAttribute("totalZakupy", totalZakupy);
        model.addAttribute("sredniRabat", String.format("%.1f", sredniRabat));
        model.addAttribute("totalZaoszczedzone", totalZaoszczedzone);
        model.addAttribute("tytul", "Raporty rabatowe klientów");
        return "zakupy/rabaty";
    }

    // Dla admina: pełna lista zakupów
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String listZakupy(Model model) {
        List<Zakup> zakupy = zakupService.findAll();
        System.out.println("DEBUG: Pobrano " + zakupy.size() + " zakupów");
        model.addAttribute("zakupy", zakupy);
        model.addAttribute("tytul", "Lista Wszystkich Zakupów");
        return "zakupy/lista-admin";
    }

    // Dla użytkownika: tylko jego zakupy
    @GetMapping("/moje")
    @PreAuthorize("isAuthenticated()")
    public String mojeZakupy(Authentication authentication, Model model) {
        String username = authentication.getName();

        System.out.println("DEBUG: Użytkownik próbuje zobaczyć swoje zakupy: " + username);

        try {
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            List<Zakup> zakupy;
            BigDecimal klientRabat = BigDecimal.ZERO; // DODAJ TĄ LINIJKĘ

            // Jeśli użytkownik ma powiązanego klienta
            if (user.getKlient() != null) {
                Long klientId = user.getKlient().getId();
                zakupy = zakupService.findByKlientId(klientId);

                // Pobierz aktualny rabat z bazy (z pominięciem cache)
                klientRabat = klientService.findById(klientId)
                        .map(Klient::getAktualnyRabat)
                        .orElse(BigDecimal.ZERO);
            } else {
                zakupy = findZakupyByUserEmail(user.getEmail());
            }

            model.addAttribute("zakupy", zakupy);
            model.addAttribute("klientRabat", klientRabat); // DODAJ TĄ LINIJKĘ
            model.addAttribute("tytul", "Moje Zakupy");
            model.addAttribute("username", username);
            return "zakupy/lista-klient";

        } catch (Exception e) {
            System.err.println("BŁĄD w mojeZakupy: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Błąd podczas ładowania zakupów: " + e.getMessage());
            return "error";
        }
    }

    // Pomocnicza metoda do znajdowania zakupów po emailu klienta
    private List<Zakup> findZakupyByUserEmail(String email) {
        // Najpierw znajdź klienta po emailu
        return klientService.findByEmail(email)
                .map(klient -> zakupService.findByKlientId(klient.getId()))
                .orElse(List.of());
    }

    // Usuwanie tylko dla admina
    @PostMapping("/usun/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteZakup(@PathVariable Long id) {
        zakupService.remove(id);
        return "redirect:/zakupy";
    }
//    @GetMapping("/generate-hash")
//    @ResponseBody
//    public String generateHash() {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        String hash = encoder.encode("testowy123");
//        return "Hash dla 'testowy123': " + hash +
//                "<br><br>SQL: UPDATE users SET password = '" + hash + "' WHERE username = 'testowy';";
//    }
}