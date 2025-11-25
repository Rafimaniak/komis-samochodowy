package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.komis.model.User;
import pl.komis.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class KlientController {

    private final UserService userService;

    @GetMapping("/klienci")
    public String listaKlientow(Model model) {
        // Pobierz wszystkich użytkowników z rolą USER
        List<User> klienci = userService.findAllUsers().stream()
                .filter(user -> "USER".equals(user.getRole()))
                .collect(Collectors.toList());

        model.addAttribute("klienci", klienci);
        model.addAttribute("tytul", "Lista Klientów");
        return "klienci";
    }
}