package pl.komis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.komis.model.User;
import pl.komis.repository.UserRepository;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("=== PRÓBA LOGOWANIA ===");
        log.info("Szukam użytkownika: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ Użytkownik nie znaleziony: {}", username);
                    return new UsernameNotFoundException("Użytkownik nie znaleziony: " + username);
                });

        log.info("✅ Znaleziono użytkownika: {}", user.getUsername());
        log.info("📧 Email: {}", user.getEmail());
        log.info("🎯 Rola: {}", user.getRole());
        log.info("🔓 Enabled: {}", user.getEnabled());
        log.info("🔑 Długość hasła: {}", user.getPassword() != null ? user.getPassword().length() : "NULL");
        log.info("=== KONIEC WCZYTYWANIA UŻYTKOWNIKA ===");

        // Sprawdź czy użytkownik jest enabled
        if (user.getEnabled() != null && !user.getEnabled()) {
            log.error("❌ Konto użytkownika {} jest wyłączone", username);
            throw new UsernameNotFoundException("Konto jest wyłączone: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getEnabled() == null ? true : user.getEnabled(), // domyślnie true jeśli null
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                getAuthorities(user.getRole())
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        String authority = "ROLE_" + (role != null ? role : "USER");
        log.info("🏷️  Nadaję uprawnienie: {}", authority);
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}