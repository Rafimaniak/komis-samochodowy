package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.komis.model.*;
import pl.komis.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/samochody")
@RequiredArgsConstructor
public class SamochodController {

    private final SamochodService samochodService;
    private final UserService userService;
    private final KlientService klientService;
    private final PracownikService pracownikService;
    private final ZakupService zakupService;

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
    public String szczegolySamochodu(@RequestParam("id") Long id, Model model, Authentication authentication) {
        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        model.addAttribute("samochod", samochod);
        model.addAttribute("tytul", samochod.getMarka() + " " + samochod.getModel());

        // DODAJ TĘ CZĘŚĆ: Wyświetlanie ceny z rabatem
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            // Znajdź użytkownika
            userService.findByUsername(username).ifPresent(user -> {
                if (user.getKlient() != null) {
                    Klient klient = user.getKlient();
                    BigDecimal rabat = klient.getAktualnyRabat();

                    // DODAJ DEBUG LOG
                    System.out.println("DEBUG: Klient ID: " + klient.getId());
                    System.out.println("DEBUG: Rabat klienta: " + rabat);

                    // PRZEKAŻ RABAT DO WIDOKU (nazwa zmiennej: klientRabat)
                    model.addAttribute("klientRabat", rabat);

                    // Oblicz cenę po rabacie
                    if (rabat != null && rabat.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal cenaBazowa = samochod.getCena();
                        BigDecimal mnoznikRabatu = BigDecimal.ONE
                                .subtract(rabat.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                        BigDecimal cenaPoRabacie = cenaBazowa.multiply(mnoznikRabatu)
                                .setScale(2, RoundingMode.HALF_UP);

                        // PRZEKAŻ CENĘ PO RABACIE
                        model.addAttribute("cenaPoRabacie", cenaPoRabacie);

                        System.out.println("DEBUG: Cena bazowa: " + cenaBazowa);
                        System.out.println("DEBUG: Cena po rabacie: " + cenaPoRabacie);
                        System.out.println("DEBUG: Mnożnik rabatu: " + mnoznikRabatu);
                    } else {
                        System.out.println("DEBUG: Brak rabatu lub rabat = 0");
                    }
                } else {
                    System.out.println("DEBUG: Użytkownik nie ma powiązanego klienta");
                    // DODAJ DOMYŚLNĄ WARTOŚĆ
                    model.addAttribute("klientRabat", BigDecimal.ZERO);
                }
            });
        } else {
            // Dla niezalogowanych
            model.addAttribute("klientRabat", BigDecimal.ZERO);
        }

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
    // Zakup samochodu - dla zalogowanych użytkowników
    @PostMapping("/kup")
    @PreAuthorize("isAuthenticated()")
    public String kupSamochod(
            @RequestParam("id") Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== ROZPOCZĘCIE ZAKUPU SAMOCHODU ID: " + id + " ===");

        Samochod samochod = samochodService.findById(id)
                .orElseThrow(() -> new RuntimeException("Samochód nie znaleziony"));

        System.out.println("Znaleziono samochód: " + samochod.getMarka() + " " + samochod.getModel());
        System.out.println("Status przed zakupem: " + samochod.getStatus());
        System.out.println("Cena samochodu: " + samochod.getCena());

        // Pobierz aktualnego użytkownika
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        System.out.println("Użytkownik: " + user.getUsername() + ", rola: " + user.getRole());

        if ("DOSTEPNY".equals(samochod.getStatus()) || "ZAREZERWOWANY".equals(samochod.getStatus())) {
            samochod.setStatus("SPRZEDANY");
            samochodService.save(samochod);

            System.out.println("Status zmieniony na: SPRZEDANY");

            try {
                // ============================================
                // TWORZENIE REKORDU ZAKUPU W BAZIE DANYCH
                // ============================================

                // 1. Znajdź lub utwórz klienta
                Klient klient = null;
                BigDecimal rabatProcent = BigDecimal.ZERO; // DODAJ TĘ LINIĘ

                // Sprawdź czy użytkownik ma powiązanego klienta
                if (user.getKlient() != null) {
                    klient = user.getKlient();
                    rabatProcent = klient.getAktualnyRabat(); // POBRZ RABAT
                    System.out.println("Użytkownik ma powiązanego klienta: " + klient.getImie() + " " + klient.getNazwisko());
                    System.out.println("Rabat klienta: " + rabatProcent + "%"); // DODAJ LOG
                } else {
                    // Spróbuj znaleźć klienta po emailu
                    klient = klientService.findByEmail(user.getEmail())
                            .orElse(null);

                    if (klient == null) {
                        // Utwórz nowego klienta na podstawie danych użytkownika
                        System.out.println("Tworzę nowego klienta dla użytkownika: " + user.getUsername());
                        klient = new Klient();
                        klient.setImie(extractFirstName(user.getUsername()));
                        klient.setNazwisko(extractLastName(user.getUsername()));
                        klient.setEmail(user.getEmail());
                        klient.setTelefon("Nie podano");
                        klient = klientService.save(klient);
                        System.out.println("Utworzono nowego klienta: " + klient.getImie() + " " + klient.getNazwisko());
                    } else {
                        rabatProcent = klient.getAktualnyRabat(); // POBRZ RABAT DLA ISTNIEJĄCEGO KLIENTA
                        System.out.println("Znaleziono istniejącego klienta. Rabat: " + rabatProcent + "%");
                    }

                    // POWIĄŻ KLIENTA Z UŻYTKOWNIKIEM
                    user.setKlient(klient);
                    userService.updateUser(user);
                    System.out.println("Powiązano klienta z użytkownikiem");
                }

                // 2. Znajdź pracownika (domyślnie pierwszy z bazy)
                Pracownik pracownik = null;
                List<Pracownik> wszyscyPracownicy = pracownikService.findAll();
                if (!wszyscyPracownicy.isEmpty()) {
                    pracownik = wszyscyPracownicy.get(0);
                    System.out.println("Użyto pracownika: " + pracownik.getImie() + " " + pracownik.getNazwisko());
                } else {
                    // Utwórz domyślnego pracownika
                    System.out.println("Brak pracowników w bazie - tworzę domyślnego...");
                    Pracownik nowyPracownik = new Pracownik();
                    nowyPracownik.setImie("Pracownik");
                    nowyPracownik.setNazwisko("Domyślny");
                    nowyPracownik.setStanowisko("Sprzedawca");
                    nowyPracownik.setEmail("pracownik@komis.pl");
                    nowyPracownik.setTelefon("111-222-333");
                    nowyPracownik.setDataZatrudnienia(LocalDate.now());
                    pracownik = pracownikService.save(nowyPracownik);
                    System.out.println("Utworzono nowego pracownika: " + pracownik.getImie() + " " + pracownik.getNazwisko());
                }

                // 3. OBLICZ CENĘ Z RABATEM - KLUCZOWA ZMIANA!
                BigDecimal cenaBazowa = samochod.getCena();
                BigDecimal cenaZRabatem = cenaBazowa;

                if (rabatProcent != null && rabatProcent.compareTo(BigDecimal.ZERO) > 0) {
                    // Oblicz mnożnik rabatu: 1 - (rabat%/100)
                    BigDecimal mnoznikRabatu = BigDecimal.ONE
                            .subtract(rabatProcent.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

                    // Oblicz cenę z rabatem
                    cenaZRabatem = cenaBazowa.multiply(mnoznikRabatu)
                            .setScale(2, RoundingMode.HALF_UP);

                    System.out.println("Zastosowano rabat: " + rabatProcent + "%");
                    System.out.println("Cena bazowa: " + cenaBazowa);
                    System.out.println("Cena po rabacie: " + cenaZRabatem);
                    System.out.println("Zaoszczędzono: " + cenaBazowa.subtract(cenaZRabatem));
                } else {
                    System.out.println("Brak rabatu dla klienta");
                }

                // 4. Stwórz obiekt Zakup z RABATEM
                Zakup zakup = new Zakup();
                zakup.setSamochod(samochod);
                zakup.setKlient(klient);
                zakup.setPracownik(pracownik);
                zakup.setDataZakupu(LocalDate.now());
                zakup.setCenaBazowa(cenaBazowa); // DODAJ CENĘ BAZOWĄ
                zakup.setCenaZakupu(cenaZRabatem); // CENA PO RABACIE
                zakup.setZastosowanyRabat(rabatProcent); // DODAJ PROCENT RABATU

                System.out.println("Tworzę zakup z danymi:");
                System.out.println("- Samochód: " + samochod.getMarka() + " " + samochod.getModel());
                System.out.println("- Klient: " + klient.getImie() + " " + klient.getNazwisko());
                System.out.println("- Rabat: " + rabatProcent + "%");
                System.out.println("- Cena bazowa: " + cenaBazowa);
                System.out.println("- Cena po rabacie: " + cenaZRabatem);
                System.out.println("- Pracownik: " + pracownik.getImie() + " " + pracownik.getNazwisko());
                System.out.println("- Data: " + LocalDate.now());

                // 5. Zapisz zakup w bazie
                Zakup zapisanyZakup = zakupService.save(zakup);
                System.out.println("Zakup zapisany w bazie! ID: " + zapisanyZakup.getId());

                // 6. Przygotuj komunikat z informacją o rabacie
                String message;
                if (rabatProcent.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal zaoszczedzone = cenaBazowa.subtract(cenaZRabatem);
                    message = String.format("Samochód kupiony! Zastosowano rabat %.0f%%. Zaoszczędziłeś: %.2f zł.",
                            rabatProcent, zaoszczedzone);
                } else {
                    message = "Samochód kupiony! Zakup zarejestrowany w systemie.";
                }

                if (user.getRole().equals("ADMIN")) {
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Samochód sprzedany i zakup zarejestrowany w systemie!");
                } else {
                    redirectAttributes.addFlashAttribute("successMessage", message);
                }

            } catch (Exception e) {
                System.err.println("BŁĄD podczas tworzenia zakupu: " + e.getMessage());
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Samochód oznaczony jako sprzedany, ale wystąpił błąd przy rejestracji zakupu: " + e.getMessage());
            }
        } else {
            System.out.println("Samochód nie jest dostępny do sprzedaży. Aktualny status: " + samochod.getStatus());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Nie można kupić samochodu. Aktualny status: " + samochod.getStatus());
        }

        System.out.println("=== ZAKOŃCZENIE ZAKUPU ===");
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

    // Pomocnicze metody do ekstrakcji imienia i nazwiska z username
    private String extractFirstName(String username) {
        if (username == null || username.isEmpty()) return "Użytkownik";
        String[] parts = username.split("\\.");
        if (parts.length > 0) return capitalize(parts[0]);
        return capitalize(username);
    }

    private String extractLastName(String username) {
        if (username == null || username.isEmpty()) return "Klient";
        String[] parts = username.split("\\.");
        if (parts.length > 1) return capitalize(parts[1]);
        return "Klient";
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}