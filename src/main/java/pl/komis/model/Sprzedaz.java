package pl.komis.model;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;


@Entity
@Table(name = "sprzedaze")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sprzedaz {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSprzedazy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_samochodu")
    private Samochod samochod;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klienta")
    private Klient klient;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracownika")
    private Pracownik pracownik;


    private LocalDate dataSprzedazy;
    private Double cenaSprzedazy;
    private String formaPlatnosci; // enum
}