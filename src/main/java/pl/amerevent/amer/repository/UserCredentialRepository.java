package pl.amerevent.amer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.amerevent.amer.model.UserCredential;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {
//	@Query("SELECT u FROM UserCredential u JOIN FETCH u.roles WHERE u.username = :username")
	Optional<UserCredential> findByUsername(String username);
}
