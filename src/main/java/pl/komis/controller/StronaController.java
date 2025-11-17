package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.komis.model.Samochod;
import pl.komis.service.SamochodService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StronaController {

    private final SamochodService samochodService;

    @GetMapping("/")
    public String stronaGlowna(Model model) {
        List<Samochod> samochody = samochodService.findAll();
        model.addAttribute("samochody", samochody);
        model.addAttribute("tytul", "Komis Samochodowy - Strona Główna");
        return "index";
    }

    @GetMapping("/samochody")
    public String listaSamochodow(Model model) {
        try {
            List<Samochod> samochody = samochodService.findAll();
            model.addAttribute("samochody", samochody);
            return "samochody";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}