package pl.amerevent.amer.repository;

import org.springframework.cglib.core.Local;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.UserDate;
import pl.amerevent.amer.model.dto.TwoCalendars;
import pl.amerevent.amer.model.dto.UserDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends UserRepositoryCustom,MongoRepository<User, String> {

	Optional<User> findByUsername(String email);

	Boolean existsByUsername(String email);
	List<User> findByAvailableDatesContains(LocalDate date);


}
