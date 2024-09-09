package pl.amerevent.amer.repository;

import jakarta.validation.constraints.NotNull;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.UserDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@EnableScan
public interface UserRepository extends UserRepositoryCustom, JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

//	@Query("SELECT u FROM User u " +
//			"WHERE (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
//			"AND (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
//			"AND (:username IS NULL OR LOWER(u.userCredential.username) LIKE LOWER(CONCAT('%', :username, '%'))) ")
//	Page<User> findUsersByCriteria(
//			@Param("firstName") String firstName,
//			@Param("lastName") String lastName,
//			@Param("username") String username,
//			Pageable pageable
//	);

	@EntityGraph(attributePaths = {"userCredential"})
	@NotNull
	Page<User> findAll(Specification<User> spec, Pageable pageable);

	@Query("SELECT ud FROM User u " +
			"JOIN u.availableDates ud " +
			"WHERE u.userCredential.username = :username " +
			"AND (:calendarFrom IS NULL OR ud.date >= :calendarFrom) " +
			"AND (:calendarTo IS NULL OR ud.date <= :calendarTo) " +
			"ORDER BY ud.date ASC")
	Page<UserDate> findAvailableDatesByUsernameAndDateRange(
			@Param("username") String username,
			@Param("calendarFrom") LocalDate calendarFrom,
			@Param("calendarTo") LocalDate calendarTo,
			Pageable pageable
	);

	@Query("SELECT u FROM User u " +
			"JOIN u.availableDates ud " +
			"WHERE ud.date = :date " +
			"AND u.id NOT IN :eventUserIds")
	List<User> findAvailableUsersByDateAndEventExclusion(
			@Param("date") LocalDate date,
			@Param("eventUserIds") Set<UUID> eventUserIds
	);

	@Query("select u from User u join fetch u.userCredential where u.userCredential.username = :username")
	Optional<User> findByUsername(@Param("username") String username);


//	Boolean existsByUsername(String email);
//	List<User> findByAvailableDatesContains(LocalDate date);


}
