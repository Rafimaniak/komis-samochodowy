package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.komis.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") String role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") String role);

    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findActiveUsers();

    @Query("SELECT u FROM User u WHERE u.enabled = false")
    List<User> findInactiveUsers();

    @Query(value = "SELECT * FROM get_user_statistics()", nativeQuery = true)
    Map<String, Object> getUserStatisticsNative();

    // Dodaj jeśli nie masz:
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
}