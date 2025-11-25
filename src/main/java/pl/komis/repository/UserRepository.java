package pl.komis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.komis.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    // Wywołanie PROCEDURY tworzenia użytkownika
    @Procedure(procedureName = "create_user")
    Long createUser(
            @Param("p_username") String username,
            @Param("p_email") String email,
            @Param("p_password") String password,
            @Param("p_role") String role,
            @Param("p_enabled") Boolean enabled
    );

    // Wywołanie PROCEDURY aktualizacji użytkownika
    @Procedure(procedureName = "update_user")
    void updateUser(
            @Param("p_id") Long id,
            @Param("p_username") String username,
            @Param("p_email") String email,
            @Param("p_password") String password,
            @Param("p_role") String role,
            @Param("p_enabled") Boolean enabled
    );

    // Wywołanie PROCEDURY usuwania użytkownika
    @Procedure(procedureName = "delete_user")
    void deleteUser(@Param("p_id") Long id);
}