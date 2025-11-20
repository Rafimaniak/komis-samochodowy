package pl.komis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promocje")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promocja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ZMIANA: było idPromocji

    @Column(nullable = false)
    private String nazwa;

    private String rodzaj;
    private Double wartosc;

    @Column(columnDefinition = "text")
    private String opis;

    private LocalDate dataRozpoczecia;
    private LocalDate dataZakonczenia;
    private Boolean aktywna;

    // TYMCZASOWO ZAKOMENTUJ RELACJĘ
    // @OneToMany(mappedBy = "promocja", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<KlientPromocja> klientPromocje = new ArrayList<>();
}