package pl.komis.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "klient")
@DynamicUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Klient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imie;
    private String nazwisko;
    private String email;
    private String telefon;

    @OneToMany(mappedBy = "klient", cascade = CascadeType.ALL)
    private List<Zakup> zakupy;

    @Column(name = "liczba_zakupow")
    private Integer liczbaZakupow = 0;

    @Column(name = "procent_premii", precision = 5, scale = 2)
    private BigDecimal procentPremii = BigDecimal.ZERO;

    @Column(name = "saldo_premii", precision = 12, scale = 2)
    private BigDecimal saldoPremii = BigDecimal.ZERO;

    @Column(name = "total_wydane", precision = 12, scale = 2)
    private BigDecimal totalWydane = BigDecimal.ZERO;

    // Metoda pomocnicza - czy klient może wykorzystać saldo?
    public boolean mozeWykorzystacSaldo(BigDecimal kwota) {
        return saldoPremii != null && saldoPremii.compareTo(kwota) >= 0;
    }

    // Metoda pomocnicza - użyj salda
    public void uzyjSalda(BigDecimal kwota) {
        if (mozeWykorzystacSaldo(kwota)) {
            saldoPremii = saldoPremii.subtract(kwota);
        }
    }
}