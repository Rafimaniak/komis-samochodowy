package pl.komis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.komis.model.Samochod;
import pl.komis.repository.SamochodRepository;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final SamochodRepository samochodRepository;

    @Override
    public void run(String... args) {
        if (samochodRepository.count() == 0) {
            // Dodaj 4 samochody
            Samochod s1 = new Samochod();
            s1.setMarka("BMW"); s1.setModel("Seria 3"); s1.setRokProdukcji(2020);
            s1.setCena(new BigDecimal("120000")); s1.setKolor("Czarny");
            s1.setPrzebieg(50000); s1.setStatus("DOSTEPNY");

            Samochod s2 = new Samochod();
            s2.setMarka("Audi"); s2.setModel("A4"); s2.setRokProdukcji(2019);
            s2.setCena(new BigDecimal("95000")); s2.setKolor("Biały");
            s2.setPrzebieg(60000); s2.setStatus("DOSTEPNY");

            // Dodaj więcej...

            samochodRepository.save(s1);
            samochodRepository.save(s2);
            // samochodRepository.save(s3);
            // samochodRepository.save(s4);

            System.out.println("Dodano samochody do bazy!");
        }
    }
}