package pl.amerevent.amer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.amerevent.amer.model.ERole;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.dto.ResponseMessage;
import pl.amerevent.amer.service.EventService;
import pl.amerevent.amer.service.EventUserService;

import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
	private final EventService eventService;
	private final EventUserService eventUserService;

	@PostMapping
	public Event createEvent(@RequestBody Event event) {
		return eventService.createEvent(event);
	}
	@PutMapping
	public ResponseEntity<Event> updateEvent(@RequestBody Event event) {
		return eventService.updateEvent(event);
	}

	@PutMapping("/userConfirmation")
	public ResponseEntity<ResponseMessage> confirmEventByUser(@RequestBody String id) {
		return eventService.confirmEventByUser(id);
	}
	@GetMapping("/{id}")
	public ResponseEntity<Event> getSingleEvent(@PathVariable String id) {
		Optional<Event> eventOpt = eventService.getSingleEvent(id);
		Optional<User> userOpt = eventUserService.findDataBaseUser();
		if (userOpt.isPresent() && eventOpt.isPresent()) {
			Event event = eventOpt.get();
			User user = userOpt.get();
			long eventAvailableUsers = Objects.nonNull(event.getAvailableUsers()) ? event.getAvailableUsers().stream().filter(usr -> usr.equals(user)).count() : 0;
			long eventAvailablePackingUsers = Objects.nonNull(event.getAvailablePackingUsers()) ? event.getAvailablePackingUsers().stream().filter(usr -> usr.equals(user)).count() : 0;
			if(user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN))){
				return new ResponseEntity<>(event, HttpStatusCode.valueOf(200));
			}
			else if (eventAvailableUsers == 0 && eventAvailablePackingUsers == 0) {
				return new ResponseEntity<>(HttpStatusCode.valueOf(404));
			}
			return new ResponseEntity<>(event, HttpStatusCode.valueOf(200));
		}
		return new ResponseEntity<>(HttpStatusCode.valueOf(404));
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseMessage> deleteUser(@PathVariable String id) {
		return eventService.deleteEvent(id);
	}

}
