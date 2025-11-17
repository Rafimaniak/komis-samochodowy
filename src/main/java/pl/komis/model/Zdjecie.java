package pl.komis.model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zdjecie {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idZdjecia;


    private String url;
    @Column(length = 1000)
    private String opis;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_samochodu")
    private Samochod samochod;
}