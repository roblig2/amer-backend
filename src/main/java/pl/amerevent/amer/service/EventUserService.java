package pl.amerevent.amer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.EventUser;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.repository.EventRepository;
import pl.amerevent.amer.repository.EventUserRepository;
import pl.amerevent.amer.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventUserService {


	private final UserRepository userRepository;
	private final EventRepository eventRepository;

//	public EventUser createEventUser(EventUser eventUser) {
//		return eventUserRepository.save(eventUser);
//	}
//
//	public List<EventUser> getEventUsersByEventId(String eventId) {
//		return eventUserRepository.findByEventId(eventId);
//	}
//
//	public List<EventUser> getEventUsersByUserId(String userId) {
//		return eventUserRepository.findByUserId(userId);
//	}
//
//	public EventUser confirmEventUser(String eventId, String userId) {
//		EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId);
//		if (eventUser != null) {
//			eventUser.setConfirmed(true);
//			return eventUserRepository.save(eventUser);
//		}
//		return null;
//	}
	public Optional<User> findDataBaseUser() {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(username);
	}
	public List<Event> findEventByUserWithSingleDate(User user, LocalDate date) {
		return eventRepository.findByAvailableUsersAndDateOrDatePacking(user,date);

	}
}
