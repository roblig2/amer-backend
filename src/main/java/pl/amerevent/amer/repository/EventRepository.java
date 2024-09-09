package pl.amerevent.amer.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.amerevent.amer.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends EventRepositoryCustom , JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {


//	@Query("SELECT e FROM Event e " +
//			"JOIN e.availableUsers au " +
//			"JOIN e.availablePackingUsers apu " +
//			"WHERE (:name IS NULL OR e.name LIKE %:name%) " +  // LIKE with %
////			"AND (:location IS NULL OR e.location LIKE %:location%) " + // LIKE with %
////			"AND (:calendarFrom IS NULL OR e.date >= :calendarFrom) " +
////			"AND (:calendarTo IS NULL OR e.date <= :calendarTo) " +
////			"AND (:isMissingPeople IS NULL OR e.isMissingPeople = :isMissingPeople) " +
//			"AND (:userId IS NULL OR au.id = :userId OR apu.id = :userId)")
//	Page<Event> findEventsByUser(
//			@Param("name") String name,
//			@Param("location") String location,
//			@Param("calendarFrom") LocalDate calendarFrom,
//			@Param("calendarTo") LocalDate calendarTo,
//			@Param("isMissingPeople") Boolean isMissingPeople,
//			@Param("userId") UUID userId,
//			Pageable pageable
//	);
//	@Query("SELECT e FROM Event e " +
//			"WHERE (:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
//			"AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
//			"AND (:calendarFrom IS NULL OR e.date >= :calendarFrom) " +
//			"AND (:calendarTo IS NULL OR e.date <= :calendarTo) " +
//			"AND (:isMissingPeople IS NULL OR e.isMissingPeople = :isMissingPeople)")
//	Page<Event> findAllEvents(
//			@Param("name") String name,
//			@Param("location") String location,
//			@Param("calendarFrom") LocalDate calendarFrom,
//			@Param("calendarTo") LocalDate calendarTo,
//			@Param("isMissingPeople") Boolean isMissingPeople,
//			Pageable pageable
//	);
	@Query("SELECT e FROM Event e " +
			"JOIN e.availableUsers au " +
			"JOIN e.availablePackingUsers apu " +
			"WHERE (au.id = :userId AND e.date = :date) " +
			"OR (apu.id = :userId AND e.datePacking = :date)")
	List<Event> findByAvailableUsersAndDateOrDatePacking(@Param("userId") UUID userId,
	                                                     @Param("date") LocalDate date);

}
