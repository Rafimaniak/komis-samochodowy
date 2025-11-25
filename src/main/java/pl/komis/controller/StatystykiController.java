package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.komis.service.SamochodService;

import java.util.List;

@Controller
@RequestMapping("/stats")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StatystykiController {

    private final SamochodService samochodService;

    @GetMapping
    public String showStats(Model model) {
        SamochodService.CarStatistics stats = samochodService.getCarStatistics();
        List<SamochodService.BrandStatistics> topBrands = samochodService.getTopBrands(10);

        model.addAttribute("stats", stats);
        model.addAttribute("topBrands", topBrands);
        model.addAttribute("tytul", "Statystyki samochodów");

        return "stats/car-stats";
    }

    @GetMapping("/sales")
    public String salesReport(
            @RequestParam(defaultValue = "2025") Integer year,
            @RequestParam(defaultValue = "11") Integer month,
            Model model) {

        List<SamochodService.MonthlySales> sales = samochodService.getMonthlySalesReport(year, month);

        model.addAttribute("sales", sales);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("tytul", "Raport sprzedaży");

        return "stats/sales-report";
    }
}