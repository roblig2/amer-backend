package pl.amerevent.amer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.dto.EventSearchRequest;
import pl.amerevent.amer.service.EventService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventListController {

	private final EventService eventService;


	@PostMapping("/eventList")
	public Page<Event> getEvents(@RequestBody EventSearchRequest eventSearchRequest) {
		return eventService.getAllEvents(eventSearchRequest);
	}
	@PostMapping("/userEventList")
	public Page<Event> getUserEvents(@RequestBody EventSearchRequest eventSearchRequest) {
		return eventService.getUserEvents(eventSearchRequest);
	}
}
