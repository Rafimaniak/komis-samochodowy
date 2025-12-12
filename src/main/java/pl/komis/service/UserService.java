package pl.komis.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.komis.model.Klient;
import pl.komis.model.User;
import pl.komis.repository.UserRepository;
import pl.komis.repository.KlientRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final KlientService klientService;
    private final KlientRepository klientRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ==================== METODY UserDetailsService ====================

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=== DEBUG: Próba zalogowania użytkownika: {}", username);

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.error("Użytkownik nie znaleziony: {}", username);
            throw new UsernameNotFoundException("Użytkownik nie znaleziony: " + username);
        }

        User user = userOptional.get();

        // DEBUG: Sprawdź format hasła
        if (user.getPassword() != null) {
            boolean isBCrypt = user.getPassword().startsWith("$2a$");
            log.info("=== DEBUG: Format hasła dla {}: {}", username, isBCrypt ? "BCRYPT OK" : "NIE BCRYPT!");
        }

        // Sprawdź czy użytkownik jest aktywny
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.error("Konto użytkownika jest nieaktywne: {}", username);
            throw new UsernameNotFoundException("Konto jest nieaktywne");
        }

        // Utwórz listę uprawnień
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Dla admina dodaj rolę ADMIN i USER
        if ("ADMIN".equals(user.getRole())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if ("USER".equals(user.getRole())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        log.debug("Załadowano użytkownika: {}, role: {}", username, authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Użytkownik nie znaleziony po email: " + email);
        }

        return loadUserByUsername(userOptional.get().getUsername());
    }

    // ==================== METODY ZAPISU (UŻYWAJĄCE PROCEDUR Z BAZY) ====================

    @Transactional
    public User save(User user) {
        log.info("=== DEBUG: Zapis użytkownika przez JPA: {}", user.getUsername());
        return userRepository.save(user);
    }

    @Transactional
    public User createUserByAdmin(User user, String rawPassword, String role) {
        log.info("=== DEBUG: Tworzenie użytkownika przez admina: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        // Zakoduj hasło
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("=== DEBUG: Hasło zakodowane do BCrypt: {}", encodedPassword);

        // Ustaw dane
        user.setPassword(encodedPassword);
        user.setRole(role != null ? role : "USER");
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        // Zapisz przez JPA (możesz zmienić na procedurę jeśli chcesz)
        User createdUser = userRepository.save(user);

        log.info("Admin utworzył użytkownika: {}", user.getUsername());
        return createdUser;
    }

    @Transactional
    public User createUser(User user, String rawPassword) {
        log.info("=== DEBUG: Tworzenie użytkownika: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        // Zakoduj hasło
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);

        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        User createdUser = userRepository.save(user);
        log.info("Utworzono użytkownika: {}", user.getUsername());
        return createdUser;
    }

    // ==================== PROSTA METODA REJESTRACJI BEZ PROCEDUR ====================

    @Transactional
    public User createSimpleUser(String username, String email, String rawPassword) {
        log.info("=== DEBUG: Tworzenie prostego użytkownika: {}", username);

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email jest już zajęty");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role("USER")
                .enabled(true)
                .createdAt(LocalDateTime.now())  // DODAJ TĘ LINIĘ
                .build();

        User savedUser = userRepository.save(user);
        log.info("Utworzono użytkownika: {}", username);
        return savedUser;
    }

    // ==================== METODY REJESTRACJI ====================

    @Transactional
    public User registerUser(User user, String rawPassword) {
        log.info("=== DEBUG: Rejestracja użytkownika: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        // Proste tworzenie użytkownika przez JPA
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("=== DEBUG: Hasło zakodowane do BCrypt: {}", encodedPassword);

        user.setPassword(encodedPassword);
        if (user.getRole() == null) {
            user.setRole("USER");
        }
        if (user.getEnabled() == null) {
            user.setEnabled(true);
        }

        User createdUser = userRepository.save(user);
        log.info("Zarejestrowano nowego użytkownika: {}", user.getUsername());
        return createdUser;
    }

    // ==================== METODY ZMIANY HASŁA ====================

    @Transactional
    public User changePassword(Long userId, String newPassword) {
        log.info("=== DEBUG: Zmiana hasła dla użytkownika ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        String encodedPassword = passwordEncoder.encode(newPassword);
        log.info("=== DEBUG: Nowe hasło zakodowane do BCrypt: {}", encodedPassword);

        user.setPassword(encodedPassword);
        User updatedUser = userRepository.save(user);

        log.info("Zmieniono hasło dla użytkownika ID: {}", userId);
        return updatedUser;
    }

    @Transactional
    public boolean verifyPassword(Long userId, String rawPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        log.info("=== DEBUG: Weryfikacja hasła dla user ID {}: {}", userId, matches ? "POPRAWNE" : "NIEPOPRAWNE");
        return matches;
    }

    // ==================== METODY AKTUALIZACJI ====================

    @Transactional
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nazwa użytkownika jest już zajęta");
        }

        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email jest już zajęty");
        }

        // Aktualizacja przez JPA
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // TYLKO jeśli podano nowe hasło (i nie jest puste)
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            log.info("=== DEBUG: Aktualizacja hasła dla użytkownika ID: {}", user.getId());
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            existingUser.setPassword(encodedPassword);
        }

        if (user.getRole() != null) {
            existingUser.setRole(user.getRole());
        }

        if (user.getEnabled() != null) {
            existingUser.setEnabled(user.getEnabled());
        }

        User updatedUser = userRepository.save(existingUser);

        log.info("Zaktualizowano użytkownika ID: {}", user.getId());
        return updatedUser;
    }

    // ==================== METODY ODCZYTU ====================

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

    public long count() {
        return userRepository.count();
    }

    // ==================== METODY USUWANIA ====================

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Użytkownik nie znaleziony");
        }
        userRepository.deleteById(id);
        log.info("Usunięto użytkownika ID: {}", id);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));
        userRepository.deleteById(user.getId());
        log.info("Usunięto użytkownika: {}", username);
    }

    // ==================== METODY WALIDACJI ====================

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
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

    // ==================== METODY KLIENTA ====================

    @Transactional
    public Klient createAndAssignKlientToUser(Long userId, String imie, String nazwisko, String email, String telefon) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            if (user.getKlient() != null) {
                log.info("Użytkownik ID {} już ma przypisanego klienta ID {}", userId, user.getKlient().getId());
                return user.getKlient();
            }

            String klientImie = imie != null ? imie : user.getUsername();
            String klientNazwisko = nazwisko != null ? nazwisko : "";
            String klientEmail = email != null ? email : user.getEmail();
            String klientTelefon = telefon != null ? telefon : "";

            Klient klient = new Klient();
            klient.setImie(klientImie);
            klient.setNazwisko(klientNazwisko);
            klient.setEmail(klientEmail);
            klient.setTelefon(klientTelefon);
            klient.setLiczbaZakupow(0);
            klient.setProcentPremii(BigDecimal.ZERO);
            klient.setSaldoPremii(BigDecimal.ZERO);
            klient.setTotalWydane(BigDecimal.ZERO);

            klient = klientRepository.save(klient);

            user.setKlient(klient);
            userRepository.save(user);

            log.info("Utworzono klienta ID {} dla użytkownika ID {}", klient.getId(), userId);
            return klient;

        } catch (Exception e) {
            log.error("Błąd podczas tworzenia klienta: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas tworzenia klienta: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void assignKlientToUser(Long userId, Long klientId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            Klient klient = klientRepository.findById(klientId)
                    .orElseThrow(() -> new RuntimeException("Klient nie znaleziony"));

            user.setKlient(klient);
            userRepository.save(user);

            log.info("Przypisano klienta ID {} do użytkownika ID {}", klientId, userId);

        } catch (Exception e) {
            log.error("Błąd podczas przypisywania klienta do użytkownika: {}", e.getMessage());
            throw new RuntimeException("Błąd podczas przypisywania klienta do użytkownika: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Klient ensureUserHasKlient(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if ("ADMIN".equals(user.getRole()) && user.getKlient() == null) {
            return null;
        }

        if (user.getKlient() != null) {
            return user.getKlient();
        }

        if (!"ADMIN".equals(user.getRole())) {
            Klient klient = new Klient();
            klient.setImie(user.getUsername());
            klient.setNazwisko("");
            klient.setEmail(user.getEmail() != null ? user.getEmail() : user.getUsername() + "@example.com");
            klient.setTelefon("");
            klient.setLiczbaZakupow(0);
            klient.setTotalWydane(BigDecimal.ZERO);
            klient.setProcentPremii(BigDecimal.ZERO);
            klient.setSaldoPremii(BigDecimal.ZERO);

            klient = klientRepository.save(klient);
            user.setKlient(klient);
            userRepository.save(user);

            return klient;
        }

        return null;
    }

    @Transactional
    public Klient ensureUserHasKlient(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        return ensureUserHasKlient(user.getId());
    }

    @Transactional
    public void validateAndFixKlientData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if ("ADMIN".equals(user.getRole())) {
            return;
        }

        if (user.getKlient() == null) {
            createAndAssignKlientToUser(userId, user.getUsername(), "", user.getEmail(), "");
            log.info("Utworzono nowego klienta dla użytkownika ID: {}", userId);
        } else {
            Klient klient = user.getKlient();

            if (klient.getSaldoPremii().compareTo(BigDecimal.ZERO) < 0 ||
                    klient.getTotalWydane().compareTo(BigDecimal.ZERO) < 0) {

                log.warn("Naprawiam ujemne wartości dla klienta ID: {}. Saldo: {}, Wydane: {}",
                        klient.getId(), klient.getSaldoPremii(), klient.getTotalWydane());

                klient.setSaldoPremii(BigDecimal.ZERO);
                klient.setTotalWydane(BigDecimal.ZERO);
                klient.setLiczbaZakupow(0);
                klient.setProcentPremii(BigDecimal.ZERO);

                klientService.save(klient);

                log.info("Naprawiono ujemne wartości dla klienta ID: {}", klient.getId());
            }
        }
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

    @Transactional
    public void validateUserCredentials(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Nieprawidłowe hasło");
        }

        log.debug("Poprawne uwierzytelnienie użytkownika: {}", username);
    }

    @Transactional
    public User resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        user.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = userRepository.save(user);

        log.info("Zresetowano hasło dla użytkownika ID: {}", userId);
        return updatedUser;
    }

    // ==================== METODY RAPORTOWANIA ====================

    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        try {
            Map<String, Object> stats = userRepository.getUserStatisticsNative();

            UserStatistics statistics = new UserStatistics(
                    ((Number) stats.get("total_users")).longValue(),
                    ((Number) stats.get("active_users")).longValue(),
                    ((Number) stats.get("inactive_users")).longValue(),
                    ((Number) stats.get("admin_users")).longValue(),
                    ((Number) stats.get("regular_users")).longValue()
            );

            log.debug("Pobrano statystyki użytkowników: {}", statistics);
            return statistics;
        } catch (Exception e) {
            log.error("Błąd podczas pobierania statystyk: {}", e.getMessage());
            return new UserStatistics(0, 0, 0, 0, 0);
        }
    }

    // ==================== METODY DIAGNOSTYCZNE ====================

    @Transactional(readOnly = true)
    public String checkUserStatus(Long userId) {
        StringBuilder status = new StringBuilder();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        status.append("Użytkownik ID: ").append(user.getId()).append("\n");
        status.append("Username: ").append(user.getUsername()).append("\n");
        status.append("Email: ").append(user.getEmail()).append("\n");
        status.append("Rola: ").append(user.getRole()).append("\n");
        status.append("Aktywny: ").append(user.getEnabled()).append("\n");

        if (user.getKlient() != null) {
            Klient klient = user.getKlient();
            status.append("\nKlient przypisany: TAK\n");
            status.append("Klient ID: ").append(klient.getId()).append("\n");
            status.append("Imię: ").append(klient.getImie()).append("\n");
            status.append("Nazwisko: ").append(klient.getNazwisko()).append("\n");
            status.append("Saldo premii: ").append(klient.getSaldoPremii()).append("\n");
            status.append("Łącznie wydano: ").append(klient.getTotalWydane()).append("\n");
        } else {
            status.append("\nKlient przypisany: NIE\n");
        }

        String statusStr = status.toString();
        log.debug("Status użytkownika ID {}:\n{}", userId, statusStr);
        return statusStr;
    }

    @Transactional(readOnly = true)
    public String checkAllUsersPasswords() {
        StringBuilder result = new StringBuilder();
        List<User> allUsers = userRepository.findAll();

        result.append("=== ANALIZA HASŁ W BAZIE ===\n");
        result.append("Liczba użytkowników: ").append(allUsers.size()).append("\n\n");

        for (User user : allUsers) {
            result.append("ID: ").append(user.getId())
                    .append(", Username: ").append(user.getUsername())
                    .append(", Email: ").append(user.getEmail())
                    .append("\n");

            if (user.getPassword() == null) {
                result.append("  HASŁO: NULL\n");
            } else if (user.getPassword().startsWith("$2a$")) {
                result.append("  FORMAT: BCRYPT OK\n");
            } else {
                result.append("  FORMAT: NIE BCRYPT\n");
                result.append("  HASH: ").append(user.getPassword()).append("\n");
            }
            result.append("  Enabled: ").append(user.getEnabled())
                    .append(", Role: ").append(user.getRole()).append("\n\n");
        }

        return result.toString();
    }

    @Transactional
    public void fixNonBCryptPasswords() {
        log.warn("=== NAPRAWA HASŁ NIE-BCRYPT ===");

        List<User> allUsers = userRepository.findAll();
        int fixedCount = 0;

        for (User user : allUsers) {
            if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
                log.warn("Naprawiam hasło dla użytkownika: {} (ID: {})", user.getUsername(), user.getId());

                // Ustaw domyślne hasło "user123"
                String defaultPassword = "user123";
                String encodedPassword = passwordEncoder.encode(defaultPassword);
                user.setPassword(encodedPassword);
                userRepository.save(user);

                fixedCount++;
                log.warn("  Nowe hasło (BCrypt): {}", encodedPassword);
            }
        }

        log.warn("=== NAPRAWIONO {} HASŁ ===", fixedCount);
    }

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

        @Override
        public String toString() {
            return String.format("UserStatistics{total=%d, active=%d, inactive=%d, admin=%d, regular=%d}",
                    totalUsers, activeUsers, inactiveUsers, adminUsers, regularUsers);
        }
    }
}