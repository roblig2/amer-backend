package pl.amerevent.amer.service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.amerevent.amer.model.*;
import pl.amerevent.amer.model.dto.EventSearchRequest;
import pl.amerevent.amer.model.dto.ResponseMessage;
import pl.amerevent.amer.repository.EventRepository;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;
	private final EventUserService eventUserService;
	private final RoleService roleService;

	public Event createEvent(Event event) {
		int usersSize = Objects.nonNull(event.getAvailableUsers()) ? event.getAvailableUsers().size() : 0;
		event.setIsMissingPeople(event.getRequiredUsers() > usersSize) ;
		return eventRepository.save(event);
	}

	public ResponseEntity<Event> updateEvent(Event event) {
		Optional<Event> eventOpt = eventRepository.findById(event.getId());
		if(eventOpt.isEmpty()){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		Event existingEvent = eventOpt.get();
		existingEvent.setName(event.getName());
		if (Objects.nonNull(event.getDate())) {
			existingEvent.setDate(event.getDate());
		}
		if (Objects.nonNull(event.getDatePacking())) {
			existingEvent.setDatePacking(event.getDatePacking());
		}
		existingEvent.setEventTime(event.getEventTime());
		existingEvent.setPackingTime(event.getPackingTime());
		existingEvent.setRequiredUsers(event.getRequiredUsers());
		existingEvent.setRequiredDrivers(event.getRequiredDrivers());
		existingEvent.setAvailableUsers(event.getAvailableUsers());
		existingEvent.setAvailablePackingUsers(event.getAvailablePackingUsers());
		if (Objects.nonNull(event.getBlackListedUsers())) {
			existingEvent.setBlackListedUsers(event.getBlackListedUsers());
		}
		if (Objects.nonNull(event.getConfirmedUsers())) {
			existingEvent.setConfirmedUsers(event.getConfirmedUsers());
		}
		int usersSize = Objects.nonNull(event.getAvailableUsers()) ? event.getAvailableUsers().size() : 0;
		existingEvent.setIsMissingPeople(event.getRequiredUsers() > usersSize) ;
		return new ResponseEntity<>(eventRepository.save(existingEvent), HttpStatus.CREATED);
	}



	public Page<Event> getAllEvents(EventSearchRequest eventSearchRequest) {
		Pageable pageable = PageRequest.of(
				eventSearchRequest.getPage(),
				eventSearchRequest.getSize(),
				Sort.by(eventSearchRequest.getSortOrder(), eventSearchRequest.getSortBy())
		);

		return eventRepository.findAll((Specification<Event>) (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (eventSearchRequest.getName() != null && !eventSearchRequest.getName().trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("name")), "%" + eventSearchRequest.getName().toLowerCase() + "%"));
			}

			if (eventSearchRequest.getLocation() != null && !eventSearchRequest.getLocation().trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("location")), "%" + eventSearchRequest.getLocation().toLowerCase() + "%"));
			}

			if (eventSearchRequest.getTwoCalendars() != null) {
				LocalDate calendarFrom = eventSearchRequest.getTwoCalendars().getCalendarFrom();
				LocalDate calendarTo = eventSearchRequest.getTwoCalendars().getCalendarTo();

				if (calendarFrom != null) {
					predicates.add(cb.greaterThanOrEqualTo(root.get("date"), calendarFrom));
				}

				if (calendarTo != null) {
					predicates.add(cb.lessThanOrEqualTo(root.get("date"), calendarTo));
				}
			}

			if (eventSearchRequest.getIsMissingPeople() != null) {
				predicates.add(cb.equal(root.get("isMissingPeople"), eventSearchRequest.getIsMissingPeople()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		}, pageable);
	}

	public Optional<Event> getSingleEvent(UUID id) {
		return eventRepository.findById(id);
	}

	public Page<Event> getUserEvents(EventSearchRequest eventSearchRequest) {
		Optional<User> userOpt = eventUserService.findDataBaseUser();

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			Pageable pageable = PageRequest.of(eventSearchRequest.getPage(),
					eventSearchRequest.getSize(),
					Sort.by(eventSearchRequest.getSortOrder(), eventSearchRequest.getSortBy()));

			Specification<Event> spec = buildSpecification(eventSearchRequest, user.getId());

			return eventRepository.findAll(spec, pageable);
		}

		return Page.empty();
	}
	public Specification<Event> buildSpecification(EventSearchRequest request,UUID userId) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (request.getName() != null) {
				predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
			}
			if (request.getLocation() != null) {
				predicates.add(cb.like(root.get("location"), "%" + request.getLocation() + "%"));
			}
			if (request.getTwoCalendars().getCalendarFrom() != null) {
				predicates.add(cb.greaterThanOrEqualTo(root.get("date"), request.getTwoCalendars().getCalendarFrom()));
			}
			if (request.getTwoCalendars().getCalendarTo() != null) {
				predicates.add(cb.lessThanOrEqualTo(root.get("date"), request.getTwoCalendars().getCalendarTo()));
			}
			if (request.getIsMissingPeople() != null) {
				predicates.add(cb.equal(root.get("isMissingPeople"), request.getIsMissingPeople()));
			}
			if (userId != null) {
				Join<Event, User> availableUsers = root.join("availableUsers", JoinType.LEFT);
				Join<Event, User> availablePackingUsers = root.join("availablePackingUsers", JoinType.LEFT);
				Predicate userPredicate = cb.or(cb.equal(availableUsers.get("id"), userId),
						cb.equal(availablePackingUsers.get("id"), userId));
				predicates.add(userPredicate);
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}

	public ResponseEntity<ResponseMessage> confirmEventByUser(UUID id) {
		Optional<User> dataBaseUser = eventUserService.findDataBaseUser();
		Optional<Event> eventOpt = eventRepository.findById(id);
		if (dataBaseUser.isPresent() && eventOpt.isPresent()) {
			User user = dataBaseUser.get();
			Event event = eventOpt.get();
			if (Objects.isNull(event.getConfirmedUsers())) {
				event.setConfirmedUsers(new HashSet<>());
			}
			event.getConfirmedUsers().add(user);
			eventRepository.save(event);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@Transactional
	public ResponseEntity<ResponseMessage> deleteEvent(UUID id) {
		ResponseMessage responseMessage = new ResponseMessage();
		Optional<Event> userOpt = eventRepository.findById(id);
		userOpt.map(event -> new ResponseEntity<>(event, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
		userOpt.ifPresent(event->{
			eventRepository.delete(event);
			responseMessage.setMessage("Usunięto Wydarzenie " + event.getName() + " z dnia " + event.getDate());

		});
		if (Objects.nonNull(responseMessage.getMessage())) {
			return new ResponseEntity<>(responseMessage, HttpStatus.OK);
		}else{
			responseMessage.setMessage("Wystąpił problem przy usuwaniu wydarzenia");
			return new ResponseEntity<>(responseMessage,HttpStatus.BAD_REQUEST);
		}

	}


}
