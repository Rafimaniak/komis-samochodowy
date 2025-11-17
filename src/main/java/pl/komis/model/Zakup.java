package pl.komis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zakup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZakupu;

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
}
