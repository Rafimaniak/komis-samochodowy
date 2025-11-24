package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class PasswordGeneratorController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/password-generator")
    public String showPasswordGenerator() {
        return "password-generator";
    }

    @GetMapping("/generate")
    public String generatePassword(@RequestParam String password, Model model) {
        String encodedPassword = passwordEncoder.encode(password);

        model.addAttribute("rawPassword", password);
        model.addAttribute("encodedPassword", encodedPassword);
        model.addAttribute("sqlUpdate",
                "UPDATE users SET password = '" + encodedPassword + "' WHERE username = 'twoja_nazwa_uzytkownika';");

        return "password-generator";
    }
}