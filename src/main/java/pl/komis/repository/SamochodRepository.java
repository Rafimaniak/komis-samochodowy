package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import pl.komis.model.Samochod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SamochodRepository extends JpaRepository<Samochod, Long> {

    // ==================== PODSTAWOWE METODY WYSZUKIWANIA ====================

    @Query("SELECT s FROM Samochod s WHERE LOWER(s.marka) = LOWER(:marka)")
    List<Samochod> findByMarkaIgnoreCase(@Param("marka") String marka);

    @Query("SELECT s FROM Samochod s WHERE LOWER(s.model) = LOWER(:model)")
    List<Samochod> findByModelIgnoreCase(@Param("model") String model);

    @Query("SELECT s FROM Samochod s WHERE s.status = :status")
    List<Samochod> findByStatus(@Param("status") String status);

    @Query("SELECT s FROM Samochod s WHERE s.cena BETWEEN :minCena AND :maxCena")
    List<Samochod> findByCenaBetween(@Param("minCena") BigDecimal minCena,
                                     @Param("maxCena") BigDecimal maxCena);

    // ==================== PROCEDURY CRUD ====================

    @Procedure(procedureName = "create_car")
    Long createCar(
            @Param("p_marka") String marka,
            @Param("p_model") String model,
            @Param("p_rok_produkcji") Integer rokProdukcji,
            @Param("p_przebieg") Integer przebieg,
            @Param("p_pojemnosc_silnika") Double pojemnoscSilnika,
            @Param("p_rodzaj_paliwa") String rodzajPaliwa,
            @Param("p_skrzynia_biegow") String skrzyniaBiegow,
            @Param("p_kolor") String kolor,
            @Param("p_cena") BigDecimal cena,
            @Param("p_status") String status,
            @Param("p_zdjecie_url") String zdjecieUrl
    );

    @Procedure(procedureName = "update_car")
    void updateCar(
            @Param("p_id") Long id,
            @Param("p_marka") String marka,
            @Param("p_model") String model,
            @Param("p_rok_produkcji") Integer rokProdukcji,
            @Param("p_przebieg") Integer przebieg,
            @Param("p_pojemnosc_silnika") Double pojemnoscSilnika,
            @Param("p_rodzaj_paliwa") String rodzajPaliwa,
            @Param("p_skrzynia_biegow") String skrzyniaBiegow,
            @Param("p_kolor") String kolor,
            @Param("p_cena") BigDecimal cena,
            @Param("p_status") String status,
            @Param("p_zdjecie_url") String zdjecieUrl
    );

    @Procedure(procedureName = "delete_car")
    void deleteCar(@Param("p_id") Long id);

    // ==================== ZAAWANSOWANE WYSZUKIWANIE - BEZPOŚREDNIE ZAPYTANIE SQL ====================

    @Query(value = "SELECT s.* FROM samochody s " +
            "WHERE (:p_marka IS NULL OR LOWER(s.marka) = LOWER(:p_marka)) " +
            "AND (:p_model IS NULL OR LOWER(s.model) = LOWER(:p_model)) " +
            "AND (:p_min_rok IS NULL OR s.rok_produkcji >= :p_min_rok) " +
            "AND (:p_max_rok IS NULL OR s.rok_produkcji <= :p_max_rok) " +
            "AND (:p_min_przebieg IS NULL OR s.przebieg >= :p_min_przebieg) " +
            "AND (:p_max_przebieg IS NULL OR s.przebieg <= :p_max_przebieg) " +
            "AND (:p_min_cena IS NULL OR s.cena >= :p_min_cena) " +
            "AND (:p_max_cena IS NULL OR s.cena <= :p_max_cena) " +
            "AND (:p_rodzaj_paliwa IS NULL OR s.rodzaj_paliwa = :p_rodzaj_paliwa) " +
            "AND (:p_status IS NULL OR s.status = :p_status)", nativeQuery = true)
    List<Samochod> searchCars(
            @Param("p_marka") String marka,
            @Param("p_model") String model,
            @Param("p_min_rok") Integer minRok,
            @Param("p_max_rok") Integer maxRok,
            @Param("p_min_przebieg") Integer minPrzebieg,
            @Param("p_max_przebieg") Integer maxPrzebieg,
            @Param("p_min_cena") BigDecimal minCena,
            @Param("p_max_cena") BigDecimal maxCena,
            @Param("p_rodzaj_paliwa") String rodzajPaliwa,
            @Param("p_status") String status
    );

    // ==================== PROSTE WYSZUKIWANIE ====================

    @Query(value = "SELECT s.* FROM samochody s " +
            "WHERE (:p_marka IS NULL OR LOWER(s.marka) = LOWER(:p_marka)) " +
            "AND (:p_model IS NULL OR LOWER(s.model) = LOWER(:p_model)) " +
            "AND (:p_status IS NULL OR s.status = :p_status)", nativeQuery = true)
    List<Samochod> searchCarsSimple(
            @Param("p_marka") String marka,
            @Param("p_model") String model,
            @Param("p_status") String status
    );

    // ==================== PROCEDURY OPERACJI BIZNESOWYCH ====================

    @Procedure(procedureName = "reserve_car")
    Map<String, Object> reserveCar(
            @Param("p_car_id") Long carId,
            @Param("p_user_id") Long userId
    );

    @Procedure(procedureName = "sell_car")
    Map<String, Object> sellCar(
            @Param("p_car_id") Long carId,
            @Param("p_user_id") Long userId,
            @Param("p_employee_id") Long employeeId,
            @Param("p_final_price") BigDecimal finalPrice
    );

    @Procedure(procedureName = "cancel_reservation")
    Map<String, Object> cancelReservation(@Param("p_car_id") Long carId);

    // ==================== FUNKCJE RAPORTOWANIA I STATYSTYK ====================

    @Query(value = "SELECT * FROM get_available_cars()", nativeQuery = true)
    List<Object[]> getAvailableCars();

    @Query(value = "SELECT * FROM get_car_stats()", nativeQuery = true)
    Map<String, Object> getCarStatistics();

    @Query(value = "SELECT * FROM get_top_brands(:limit_count)", nativeQuery = true)
    List<Object[]> getTopBrands(@Param("limit_count") Integer limitCount);

    @Query(value = "SELECT * FROM get_monthly_sales_report_func(:p_year, :p_month)", nativeQuery = true)
    List<Object[]> getMonthlySalesReport(
            @Param("p_year") Integer year,
            @Param("p_month") Integer month
    );
}