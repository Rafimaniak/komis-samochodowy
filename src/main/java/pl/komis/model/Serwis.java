package pl.komis.model;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;


@Entity
@Table(name = "serwis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Serwis {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_samochodu")
    private Samochod samochod;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pracownika")
    private Pracownik pracownik;


    @Column(columnDefinition = "text")
    private String opisUslugi;
    private Double koszt;
    private LocalDate dataSerwisu;
}