package pl.komis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "klient")
@DynamicUpdate
@Getter
@Setter  // ZAMIENIAMY RĘCZNE GETTERY/SETTERY NA LOMBOK
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

    // NOWE POLA DO OBSŁUGI SYSTEMU RABATOWEGO
    @Column(name = "liczba_zakupow")
    private Integer liczbaZakupow = 0;

    @Column(name = "aktualny_rabat", precision = 5, scale = 2)
    private BigDecimal aktualnyRabat = BigDecimal.ZERO;

    public Klient() {}

    // --- GETTERY I SETTERY ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public List<Zakup> getZakupy() {
        return zakupy;
    }

    public void setZakupy(List<Zakup> zakupy) {
        this.zakupy = zakupy;
    }

    public Integer getLiczbaZakupow() {
        return liczbaZakupow;
    }

    public void setLiczbaZakupow(Integer liczbaZakupow) {
        this.liczbaZakupow = liczbaZakupow;
    }

    public BigDecimal getAktualnyRabat() {
        return aktualnyRabat;
    }

    public void setAktualnyRabat(BigDecimal aktualnyRabat) {
        this.aktualnyRabat = aktualnyRabat;
    }
}