package pl.komis.model;


import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pracownik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imie;
    private String nazwisko;
    private String stanowisko;
    private String telefon;
    private String email;
    private LocalDate dataZatrudnienia;

    @OneToMany(mappedBy = "pracownik")
    private List<Sprzedaz> sprzedaze = new ArrayList<>();

    @OneToMany(mappedBy = "pracownik")
    private List<Serwis> serwisy = new ArrayList<>();

    @OneToMany(mappedBy = "pracownik")
    private List<Zakup> zakupy = new ArrayList<>();
}