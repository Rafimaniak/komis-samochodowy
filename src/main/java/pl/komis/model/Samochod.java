package pl.komis.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "samochody")
public class Samochod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marka;
    private String model;
    private int rokProdukcji;
    private int przebieg;

    @Column(name = "pojemnosc_silnika")
    private Double pojemnoscSilnika;

    @Column(name = "rodzaj_paliwa")
    private String rodzajPaliwa;

    @Column(name = "skrzynia_biegow")
    private String skrzyniaBiegow;

    private String kolor;
    private BigDecimal cena;
    private String status;

    @Column(name = "data_dodania")
    private LocalDate dataDodania;

    // DODANE: Pole na zdjęcie
    @Column(name = "zdjecie_url")
    private String zdjecieUrl;

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

    public int getPrzebieg() {
        return przebieg;
    }

    public void setPrzebieg(int przebieg) {
        this.przebieg = przebieg;
    }

    public Double getPojemnoscSilnika() {
        return pojemnoscSilnika;
    }

    public void setPojemnoscSilnika(Double pojemnoscSilnika) {
        this.pojemnoscSilnika = pojemnoscSilnika;
    }

    public String getRodzajPaliwa() {
        return rodzajPaliwa;
    }

    public void setRodzajPaliwa(String rodzajPaliwa) {
        this.rodzajPaliwa = rodzajPaliwa;
    }

    public String getSkrzyniaBiegow() {
        return skrzyniaBiegow;
    }

    public void setSkrzyniaBiegow(String skrzyniaBiegow) {
        this.skrzyniaBiegow = skrzyniaBiegow;
    }

    public String getKolor() {
        return kolor;
    }

    public void setKolor(String kolor) {
        this.kolor = kolor;
    }

    public BigDecimal getCena() {
        return cena;
    }

    public void setCena(BigDecimal cena) {
        this.cena = cena;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataDodania() {
        return dataDodania;
    }

    public void setDataDodania(LocalDate dataDodania) {
        this.dataDodania = dataDodania;
    }

    public String getZdjecieUrl() {
        return zdjecieUrl;
    }

    public void setZdjecieUrl(String zdjecieUrl) {
        this.zdjecieUrl = zdjecieUrl;
    }
}