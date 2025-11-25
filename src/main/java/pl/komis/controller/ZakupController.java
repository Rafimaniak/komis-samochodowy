package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.komis.model.Zakup;
import pl.komis.service.*;

@Controller
@RequestMapping("/zakupy")
@RequiredArgsConstructor
public class ZakupController {

    private final ZakupService zakupService;
    private final SamochodService samochodService;
    private final KlientService klientService;
    private final PracownikService pracownikService;

    @GetMapping
    public String listZakupy(Model model) {
        model.addAttribute("zakupy", zakupService.findAll());
        model.addAttribute("tytul", "Lista Zakupów");
        return "zakupy_lista";
    }

    @GetMapping("/dodaj")
    public String showAddForm(Model model) {
        model.addAttribute("zakup", new Zakup());
        model.addAttribute("samochody", samochodService.findAll());
        model.addAttribute("klienci", klientService.findAll());
        model.addAttribute("pracownicy", pracownikService.findAll());
        return "zakupy_form";
    }

    @PostMapping("/dodaj")
    public String addZakup(@ModelAttribute Zakup zakup) {
        zakupService.save(zakup);
        return "redirect:/zakupy";
    }

    @GetMapping("/edytuj/{id}")
    public String editZakup(@PathVariable Long id, Model model) {
        model.addAttribute("zakup", zakupService.getById(id));
        model.addAttribute("samochody", samochodService.findAll());
        model.addAttribute("klienci", klientService.findAll());
        model.addAttribute("pracownicy", pracownikService.findAll());
        return "zakupy_form";
    }

    @PostMapping("/edytuj/{id}")
    public String updateZakup(@PathVariable Long id, @ModelAttribute Zakup zakup) {
        zakup.setId(id);
        zakupService.save(zakup);
        return "redirect:/zakupy";
    }

    @GetMapping("/usun/{id}")
    public String deleteZakup(@PathVariable Long id) {
        zakupService.remove(id);
        return "redirect:/zakupy";
    }
}
