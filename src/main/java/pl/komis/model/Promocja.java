package pl.komis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promocja")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promocja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPromocji;

    @Column(nullable = false)
    private String nazwa;

    /**
     * Możesz zamienić na Enum jeśli chcesz --> @Enumerated(EnumType.STRING)
     * np. RODZAJ: RABAT_PROCENTOWY, RABAT_STALA, BONUS_PUNKTOWY
     */
    private String rodzaj;

    private Double wartosc;

    @Column(columnDefinition = "text")
    private String opis;

    private LocalDate dataRozpoczecia;

    private LocalDate dataZakonczenia;

    private Boolean aktywna;

    // relacja do tabeli łącznikowej z klientami
    @OneToMany(mappedBy = "promocja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KlientPromocja> klientPromocje = new ArrayList<>();

    // convenience method
    public void addKlientPromocja(KlientPromocja kp) {
        klientPromocje.add(kp);
        kp.setPromocja(this);
    }

    public void removeKlientPromocja(KlientPromocja kp) {
        klientPromocje.remove(kp);
        kp.setPromocja(null);
    }
}
