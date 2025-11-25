package pl.komis.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.komis.model.User;
import pl.komis.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ==================== METODY REJESTRACJI I TWORZENIA ====================

    public User registerUser(User user, String rawPassword) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        String role = user.getRole() != null ? user.getRole() : "USER";

        // Użycie procedury zamiast standardowego save
        Long newUserId = userRepository.createUser(
                user.getUsername(),
                user.getEmail(),
                encodedPassword,
                role,
                true
        );

        // Pobierz utworzonego użytkownika
        return userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas tworzenia użytkownika"));
    }

    public User createUserByAdmin(User user, String rawPassword, String role) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Użycie procedury tworzenia użytkownika
        Long newUserId = userRepository.createUser(
                user.getUsername(),
                user.getEmail(),
                encodedPassword,
                role,
                true
        );

        return userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas tworzenia użytkownika"));
    }

    public User createUser(User user, String rawPassword) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        String role = user.getRole() != null ? user.getRole() : "USER";

        // Użycie procedury tworzenia użytkownika
        Long newUserId = userRepository.createUser(
                user.getUsername(),
                user.getEmail(),
                encodedPassword,
                role,
                true
        );

        return userRepository.findById(newUserId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas tworzenia użytkownika"));
    }

    // ==================== METODY ODCZYTU ====================

    public List<User> findAllUsers() { return userRepository.findAll(); }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ==================== METODY AKTUALIZACJI ====================

    public User updateUser(User user) {
        // Sprawdź czy użytkownik istnieje
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Sprawdź unikalność username (jeśli się zmienił)
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }

        // Sprawdź unikalność email (jeśli się zmienił)
        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        // Zachowaj hasło jeśli nie zostało zmienione
        String password = user.getPassword();
        if (password == null || password.isEmpty()) {
            password = existingUser.getPassword();
        } else {
            // Jeśli podano nowe hasło, zakoduj je
            password = passwordEncoder.encode(password);
        }

        // Użycie procedury aktualizacji
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                password,
                user.getRole(),
                user.getEnabled() != null ? user.getEnabled() : true
        );

        return userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Błąd podczas aktualizacji użytkownika"));
    }

    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        String encodedPassword = passwordEncoder.encode(newPassword);

        // Użycie procedury aktualizacji z nowym hasłem
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                encodedPassword,
                user.getRole(),
                user.getEnabled()
        );

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas zmiany hasła"));
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        boolean newStatus = !user.getEnabled();

        // Użycie procedury aktualizacji statusu
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                newStatus
        );

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas zmiany statusu użytkownika"));
    }

    public User activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Użycie procedury aktualizacji - aktywacja
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                true
        );

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas aktywacji użytkownika"));
    }

    public User deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Użycie procedury aktualizacji - deaktywacja
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                false
        );

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas deaktywacji użytkownika"));
    }

    public User changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Użycie procedury aktualizacji roli
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                newRole,
                user.getEnabled()
        );

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas zmiany roli użytkownika"));
    }

    // ==================== METODY USUWANIA ====================

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Użytkownik nie znaleziony");
        }
        // Użycie procedury usuwania
        userRepository.deleteUser(id);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        // Użycie procedury usuwania
        userRepository.deleteUser(user.getId());
    }

    // ==================== METODY WALIDACJI I SPRAWDZANIA ====================

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    public long count() {
        return userRepository.count();
    }

    public long countByRole(String role) {
        return userRepository.countByRole(role);
    }

    public long countActiveUsers() {
        return userRepository.findActiveUsers().size();
    }

    public long countInactiveUsers() {
        return userRepository.findInactiveUsers().size();
    }

    // ==================== METODY SPECJALNE ====================

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findActiveUsers() {
        return userRepository.findActiveUsers();
    }

    public List<User> findInactiveUsers() {
        return userRepository.findInactiveUsers();
    }

    public List<User> findAdmins() {
        return findByRole("ADMIN");
    }

    public List<User> findUsers() {
        return findByRole("USER");
    }

    public boolean isUserActive(Long userId) {
        return userRepository.findById(userId)
                .map(User::getEnabled)
                .orElse(false);
    }

    public boolean isUserAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> "ADMIN".equals(user.getRole()))
                .orElse(false);
    }

    public boolean isUserRegular(Long userId) {
        return userRepository.findById(userId)
                .map(user -> "USER".equals(user.getRole()))
                .orElse(false);
    }

    // ==================== METODY POMOCNICZE ====================

    public void validateUserCredentials(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Nieprawidłowe hasło");
        }
    }

    public boolean verifyPassword(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public User resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        String encodedPassword = passwordEncoder.encode(newPassword);

        // Użycie procedury aktualizacji hasła
        userRepository.updateUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                encodedPassword,
                user.getRole(),
                user.getEnabled()
        );

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Błąd podczas resetowania hasła"));
    }

    // ==================== METODY RAPORTOWANIA ====================

    public UserStatistics getUserStatistics() {
        Map<String, Object> stats = userRepository.getUserStatisticsNative();

        return new UserStatistics(
                ((Number) stats.get("total_users")).longValue(),
                ((Number) stats.get("active_users")).longValue(),
                ((Number) stats.get("inactive_users")).longValue(),
                ((Number) stats.get("admin_users")).longValue(),
                ((Number) stats.get("regular_users")).longValue()
        );
    }

    // Klasa pomocnicza dla statystyk
    @Getter
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long inactiveUsers;
        private final long adminUsers;
        private final long regularUsers;

        public UserStatistics(long totalUsers, long activeUsers, long inactiveUsers, long adminUsers, long regularUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.adminUsers = adminUsers;
            this.regularUsers = regularUsers;
        }
    }
}