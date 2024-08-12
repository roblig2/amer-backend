package pl.amerevent.amer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.dto.TwoCalendars;
import pl.amerevent.amer.model.dto.UserDateDto;
import pl.amerevent.amer.model.dto.WorkDayDto;
import pl.amerevent.amer.model.dto.WorkDayListDto;
import pl.amerevent.amer.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserWorkdaysController {
	private final UserService userService;


	@PostMapping("/workdays")
	public ResponseEntity<User> addWorkDays(@RequestBody WorkDayDto workday) {
		return userService.addWorkDays(workday);
	}
	@PostMapping("/workdaysList")
	public Page<UserDateDto> getWorkDays(@RequestBody WorkDayListDto workDayListDto) {
		return userService.getWorkDays(workDayListDto);
	}

}
