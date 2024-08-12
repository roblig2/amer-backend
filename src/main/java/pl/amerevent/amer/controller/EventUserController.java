package pl.amerevent.amer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.amerevent.amer.model.EventUser;
import pl.amerevent.amer.service.EventUserService;

import java.util.List;

@RestController
@RequestMapping("/api/event-users")
@RequiredArgsConstructor
public class EventUserController {

//
//	private final EventUserService eventUserService;
//
//	@PostMapping
//	public EventUser createEventUser(@RequestBody EventUser eventUser) {
//		return eventUserService.createEventUser(eventUser);
//	}
//
//	@GetMapping("/event/{eventId}")
//	public List<EventUser> getEventUsersByEventId(@PathVariable String eventId) {
//		return eventUserService.getEventUsersByEventId(eventId);
//	}
//
//	@GetMapping("/user/{userId}")
//	public List<EventUser> getEventUsersByUserId(@PathVariable String userId) {
//		return eventUserService.getEventUsersByUserId(userId);
//	}
//
//	@PostMapping("/confirm")
//	public EventUser confirmEventUser(@RequestParam String eventId, @RequestParam String userId) {
//		return eventUserService.confirmEventUser(eventId, userId);
//	}
}
