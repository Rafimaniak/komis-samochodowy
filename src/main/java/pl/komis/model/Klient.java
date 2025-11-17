package pl.komis.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
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

    public Klient() {}

    // --- Gettery i settery ---
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
}
