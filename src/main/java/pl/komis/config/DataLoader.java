package pl.komis.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.komis.model.Samochod;
import pl.komis.repository.SamochodRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final SamochodRepository samochodRepository;

    @Override
    public void run(String... args) {
        if (samochodRepository.count() == 0) {
            // Dodaj samochody z wszystkimi wymaganymi polami
            Samochod s1 = new Samochod();
            s1.setMarka("BMW");
            s1.setModel("Seria 3");
            s1.setRokProdukcji(2020);
            s1.setPrzebieg(50000);
            s1.setPojemnoscSilnika(2.0);
            s1.setRodzajPaliwa("Benzyna");
            s1.setSkrzyniaBiegow("Automatyczna");
            s1.setKolor("Czarny");
            s1.setCena(new BigDecimal("120000"));
            s1.setStatus("DOSTEPNY");
            s1.setDataDodania(LocalDate.now());

            Samochod s2 = new Samochod();
            s2.setMarka("Audi");
            s2.setModel("A4");
            s2.setRokProdukcji(2019);
            s2.setPrzebieg(60000);
            s2.setPojemnoscSilnika(2.0);
            s2.setRodzajPaliwa("Diesel");
            s2.setSkrzyniaBiegow("Manualna");
            s2.setKolor("Biały");
            s2.setCena(new BigDecimal("95000"));
            s2.setStatus("DOSTEPNY");
            s2.setDataDodania(LocalDate.now());

            samochodRepository.save(s1);
            samochodRepository.save(s2);

            System.out.println("Dodano samochody do bazy!");
        }
    }
}