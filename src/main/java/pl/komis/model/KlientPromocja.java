package pl.komis.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "klienci_promocje")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KlientPromocja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // klient, który otrzymał promocję
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_klienta", nullable = false)
    private Klient klient;

    // promocja, która została przyznana
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_promocji", nullable = false)
    private Promocja promocja;

    private LocalDate dataPrzyznania;
}