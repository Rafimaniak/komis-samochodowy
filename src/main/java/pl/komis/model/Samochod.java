package pl.komis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "samochod")
public class Samochod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marka;
    private String model;
    private int rokProdukcji;
    private BigDecimal cena;
    private String kolor;
    private int przebieg;

    // DODANE POLE STATUS
    private String status; // np: "DOSTEPNY", "SPRZEDANY", "ZAREZERWOWANY"

    @OneToMany(mappedBy = "samochod", cascade = CascadeType.ALL)
    private List<Zakup> zakupy;

    public Samochod() {}

    // --- Gettery i settery ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getRokProdukcji() {
        return rokProdukcji;
    }

    public void setRokProdukcji(int rokProdukcji) {
        this.rokProdukcji = rokProdukcji;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public String getKolor() {
        return kolor;
    }

    public void setKolor(String kolor) {
        this.kolor = kolor;
    }

    public int getPrzebieg() {
        return przebieg;
    }

    public void setPrzebieg(int przebieg) {
        this.przebieg = przebieg;
    }

    // GETTER I SETTER DLA STATUS
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Zakup> getZakupy() {
        return zakupy;
    }

    public void setZakupy(List<Zakup> zakupy) {
        this.zakupy = zakupy;
    }
}