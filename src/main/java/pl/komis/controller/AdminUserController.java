package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.komis.model.User;
import pl.komis.service.UserService;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("tytul", "Zarządzanie użytkownikami");
        return "admin/users";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("tytul", "Dodaj nowego użytkownika");
        model.addAttribute("roles", Arrays.asList("USER", "ADMIN"));
        return "admin/user-form";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute User user,
                             @RequestParam("password") String password,
                             @RequestParam("role") String role,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.createUserByAdmin(user, password, role);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został utworzony pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas tworzenia użytkownika: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        model.addAttribute("user", user);
        model.addAttribute("tytul", "Edytuj użytkownika");
        model.addAttribute("roles", Arrays.asList("USER", "ADMIN"));
        return "admin/user-form";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute User user,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             RedirectAttributes redirectAttributes) {
        try {
            User existingUser = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            existingUser.setEnabled(user.getEnabled());

            // Zmiana hasła jeśli podano nowe
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(newPassword));
            }

            userService.updateUser(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Użytkownik został zaktualizowany pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas aktualizacji użytkownika: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/change-password/{id}")
    public String showChangePasswordForm(@PathVariable("id") Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        model.addAttribute("user", user);
        model.addAttribute("tytul", "Zmiana hasła użytkownika");
        return "admin/change-password";
    }

    @PostMapping("/change-password/{id}")
    public String changePassword(@PathVariable("id") Long id,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Hasła nie są identyczne");
                return "redirect:/admin/users/change-password/" + id;
            }

            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(user);

            redirectAttributes.addFlashAttribute("successMessage", "Hasło zostało zmienione pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas zmiany hasła: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            user.setEnabled(!user.getEnabled());
            userService.updateUser(user);

            String status = user.getEnabled() ? "aktywowane" : "dezaktywowane";
            redirectAttributes.addFlashAttribute("successMessage",
                    "Konto użytkownika " + user.getUsername() + " zostało " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas zmiany statusu użytkownika: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Użytkownik " + user.getUsername() + " został usunięty pomyślnie");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Błąd podczas usuwania użytkownika: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}