package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.komis.model.KlientPromocja;

import java.util.List;

public interface KlientPromocjaRepository extends JpaRepository<KlientPromocja, Long> {

    @Query("SELECT kp FROM KlientPromocja kp WHERE kp.klient.id = :klientId")
    List<KlientPromocja> findByKlientId(@Param("klientId") Long klientId);

    @Query("SELECT kp FROM KlientPromocja kp WHERE kp.promocja.id = :promocjaId")
    List<KlientPromocja> findByPromocjaId(@Param("promocjaId") Long promocjaId);
}