package pl.amerevent.amer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.dto.EventSearchRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryCustom {
	Page<Event> findAllEvents(EventSearchRequest eventSearchRequest);

	Page<Event> findEventsByUser(EventSearchRequest eventSearchRequest,String id);

	List<Event> findByAvailableUsersAndDateOrDatePacking(User user, LocalDate date);

}
