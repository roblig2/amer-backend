package pl.amerevent.amer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.amerevent.amer.model.ERole;
import pl.amerevent.amer.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, String> {
	Optional<Role> findByName(ERole name);
//	@Query("select r from Role r join fetch r.userCredential where r.userCredential.username = :username")
//	Set<Role> findByUsername(String username);

	Set<Role> findAllByNameIn(List<ERole> names);
}
