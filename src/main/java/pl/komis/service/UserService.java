package pl.komis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.komis.model.User;
import pl.komis.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        // Domyślnie ustaw rolę USER, jeśli nie podano
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    public User createUserByAdmin(User user, String rawPassword, String role) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(role);

        return userRepository.save(user);
    }

    public User createUser(User user, String rawPassword) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        // Domyślnie ustaw rolę USER, jeśli nie podano
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        return userRepository.save(user);
    }

    // ==================== METODY ODCZYTU ====================

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

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
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        // Zachowaj datę utworzenia
        user.setCreatedAt(existingUser.getCreatedAt());

        return userRepository.save(user);
    }

    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setEnabled(!user.getEnabled());
        return userRepository.save(user);
    }

    public User activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setEnabled(false);
        return userRepository.save(user);
    }

    public User changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setRole(newRole);
        return userRepository.save(user);
    }

    // ==================== METODY USUWANIA ====================

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Użytkownik nie znaleziony");
        }
        userRepository.deleteById(id);
    }

    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        userRepository.delete(user);
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
        return userRepository.findAll().stream()
                .filter(user -> role.equals(user.getRole()))
                .count();
    }

    public long countActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::getEnabled)
                .count();
    }

    public long countInactiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getEnabled())
                .count();
    }

    // ==================== METODY SPECJALNE ====================

    public List<User> findByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> findActiveUsers() {
        return userRepository.findAll().stream()
                .filter(User::getEnabled)
                .collect(Collectors.toList());
    }

    public List<User> findInactiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !user.getEnabled())
                .collect(Collectors.toList());
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

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    // ==================== METODY RAPORTOWANIA ====================

    public UserStatistics getUserStatistics() {
        long totalUsers = count();
        long activeUsers = countActiveUsers();
        long inactiveUsers = countInactiveUsers();
        long adminUsers = countByRole("ADMIN");
        long regularUsers = countByRole("USER");

        return new UserStatistics(totalUsers, activeUsers, inactiveUsers, adminUsers, regularUsers);
    }

    // Klasa pomocnicza dla statystyk
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

        // Gettery
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getInactiveUsers() { return inactiveUsers; }
        public long getAdminUsers() { return adminUsers; }
        public long getRegularUsers() { return regularUsers; }
    }
}