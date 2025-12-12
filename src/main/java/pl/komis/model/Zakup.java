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
@Builder(toBuilder = true)  // DODAJ toBuilder = true
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
    private BigDecimal cenaZakupu; // Cena ostateczna (po ewentualnym wykorzystaniu salda)

    @Column(name = "cena_bazowa", precision = 12, scale = 2)
    private BigDecimal cenaBazowa; // Cena przed zastosowaniem salda

    @Column(name = "zastosowany_rabat", precision = 5, scale = 2)
    private BigDecimal zastosowanyRabat = BigDecimal.ZERO; // Procent premii naliczonej

    @Column(name = "naliczona_premia", precision = 12, scale = 2)
    private BigDecimal naliczonaPremia = BigDecimal.ZERO; // Kwota premii z tego zakupu

    @Column(name = "wykorzystane_saldo", precision = 12, scale = 2)
    private BigDecimal wykorzystaneSaldo = BigDecimal.ZERO; // Kwota wykorzystanego salda

    // Alternatywnie: użyj @Builder.Default dla pól z domyślnymi wartościami
    // @Builder.Default
    // private BigDecimal naliczonaPremia = BigDecimal.ZERO;

    // Metoda pomocnicza do obliczania zaoszczędzonej kwoty
    public BigDecimal getZaoszczedzonaKwota() {
        if (cenaBazowa != null && cenaZakupu != null) {
            return cenaBazowa.subtract(cenaZakupu);
        }
        return BigDecimal.ZERO;
    }

    // Metoda pomocnicza - czy wykorzystano saldo?
    public boolean czyWykorzystanoSaldo() {
        return wykorzystaneSaldo != null && wykorzystaneSaldo.compareTo(BigDecimal.ZERO) > 0;
    }
}