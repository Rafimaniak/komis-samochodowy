package pl.komis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class KomisApplication {

    public static void main(String[] args) {
//        // Tymczasowe generowanie haseł
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        System.out.println("=== GENEROWANIE HASEŁ BCrypt ===");
//        System.out.println("admin: " + encoder.encode("admin"));
//        System.out.println("user: " + encoder.encode("user"));
//        System.out.println("testowy: " + encoder.encode("testowy"));
//        System.out.println("=== KONIEC ===");

        SpringApplication.run(KomisApplication.class, args);
    }
}