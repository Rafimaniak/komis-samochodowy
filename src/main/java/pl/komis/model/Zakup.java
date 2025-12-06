package pl.komis.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Table(name = "zakupy")
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zakup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_samochodu")
    private Samochod samochod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klienta")
    private Klient klient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracownika")
    private Pracownik pracownik;

    private LocalDate dataZakupu;

    @Column(precision = 12, scale = 2)
    private BigDecimal cenaZakupu;

    // NOWE POLE: cena przed zastosowaniem rabatu
    @Column(name = "cena_bazowa", precision = 12, scale = 2)
    private BigDecimal cenaBazowa;

    // NOWE POLE: procent rabatu zastosowanego w tym zakupie
    @Column(name = "zastosowany_rabat", precision = 5, scale = 2)
    private BigDecimal zastosowanyRabat = BigDecimal.ZERO;

    // Metoda pomocnicza do obliczania zaoszczędzonej kwoty
    public BigDecimal getZaoszczedzonaKwota() {
        if (cenaBazowa != null && cenaZakupu != null) {
            return cenaBazowa.subtract(cenaZakupu);
        }
        return BigDecimal.ZERO;
    }
}