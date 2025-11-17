package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Samochod;

import java.math.BigDecimal;
import java.util.List;

public interface SamochodRepository extends JpaRepository<Samochod, Long> {

    // Poprawione metody z @Query dla większej kontroli
    @Query("SELECT s FROM Samochod s WHERE LOWER(s.marka) = LOWER(:marka)")
    List<Samochod> findByMarkaIgnoreCase(@Param("marka") String marka);

    @Query("SELECT s FROM Samochod s WHERE LOWER(s.model) = LOWER(:model)")
    List<Samochod> findByModelIgnoreCase(@Param("model") String model);

    @Query("SELECT s FROM Samochod s WHERE s.status = :status")
    List<Samochod> findByStatus(@Param("status") String status);

    @Query("SELECT s FROM Samochod s WHERE s.cena BETWEEN :minCena AND :maxCena")
    List<Samochod> findByCenaBetween(@Param("minCena") BigDecimal minCena,
                                     @Param("maxCena") BigDecimal maxCena);
}