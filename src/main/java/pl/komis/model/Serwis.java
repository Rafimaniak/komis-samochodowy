package pl.komis.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "serwis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Serwis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_samochodu")
    private Samochod samochod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pracownika")
    private Pracownik pracownik;

    @Column(columnDefinition = "text")
    private String opisUslugi;

    @Column(precision = 10, scale = 2)
    private BigDecimal koszt;

    private LocalDate dataSerwisu;

    // DODANE: Pole obliczalne dla statusu
    @Transient
    public String getStatus() {
        if (koszt == null) {
            return "ZAREZERWOWANY";
        } else {
            return "ZAKOŃCZONY";
        }
    }

    // DODANE: Czy serwis jest zarezerwowany
    @Transient
    public boolean isZarezerwowany() {
        return koszt == null;
    }

    // DODANE: Czy serwis jest zakończony
    @Transient
    public boolean isZakonczony() {
        return koszt != null;
    }
}