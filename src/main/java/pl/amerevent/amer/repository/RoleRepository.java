package pl.amerevent.amer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.amerevent.amer.model.ERole;
import pl.amerevent.amer.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(ERole name);

	Set<Role> findAllByNameIn(List<ERole> names);
}
