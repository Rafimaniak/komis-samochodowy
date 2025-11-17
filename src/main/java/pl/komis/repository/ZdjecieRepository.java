package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Zdjecie;

import java.util.List;

public interface ZdjecieRepository extends JpaRepository<Zdjecie, Long> {

    @Query("SELECT z FROM Zdjecie z WHERE z.samochod.id = :samochodId")
    List<Zdjecie> findBySamochodId(@Param("samochodId") Long samochodId);
}