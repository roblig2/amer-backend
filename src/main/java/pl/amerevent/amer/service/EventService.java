package pl.amerevent.amer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
		return eventRepository.findAllEvents(eventSearchRequest);
	}

	public Optional<Event> getSingleEvent(String id) {
		return eventRepository.findById(id);
	}

	public Page<Event> getUserEvents(EventSearchRequest eventSearchRequest) {
		Optional<User> userOpt = eventUserService.findDataBaseUser();
		if(userOpt.isPresent()){
			User user = userOpt.get();
			return eventRepository.findEventsByUser(eventSearchRequest,user.getId());
			}
		return Page.empty();
	}


	public ResponseEntity<ResponseMessage> confirmEventByUser(String id) {
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
	public ResponseEntity<ResponseMessage> deleteEvent(String id) {
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
