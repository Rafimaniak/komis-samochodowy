package pl.komis.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.komis.model.User;
import pl.komis.repository.UserRepository;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/api/debug/password")
    public String debugPassword(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null) {
            return "User not found: " + username;
        }

        boolean matches = passwordEncoder.matches(password, user.getPassword());

        return String.format(
                "Username: %s<br>Stored password: %s<br>Input password: %s<br>Matches: %s<br>Password length: %d",
                username,
                user.getPassword(),
                password,
                matches,
                user.getPassword().length()
        );
    }

    @GetMapping("/api/debug/users")
    public String debugUsers() {
        StringBuilder result = new StringBuilder();
        result.append("<h3>All Users:</h3>");

        userRepository.findAll().forEach(user -> {
            result.append(String.format(
                    "Username: %s, Email: %s, Role: %s, Enabled: %s<br>",
                    user.getUsername(), user.getEmail(), user.getRole(), user.getEnabled()
            ));
        });

        return result.toString();
    }
}