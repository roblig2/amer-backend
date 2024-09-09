package pl.amerevent.amer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.UserCredential;
import pl.amerevent.amer.model.UserDate;
import pl.amerevent.amer.repository.EventRepository;
import pl.amerevent.amer.repository.UserCredentialRepository;
import pl.amerevent.amer.repository.UserDateRepository;
import pl.amerevent.amer.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventUserService {


	private final UserCredentialRepository userCredentialRepository;
	private final UserRepository userRepository;
	private final UserDateRepository userDateRepository;
	private final EventRepository eventRepository;

	@Transactional
	public Optional<User> findDataBaseUser() {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<UserCredential> userCredentialOpt = userCredentialRepository.findByUsername(username);
		return userCredentialOpt.map(userCredential -> userRepository.findByUsername(userCredential.getUsername())).orElse(null);
	}
	public UserDate addUserDate(UserDate userDate){
		return userDateRepository.save(userDate);
	}
	public List<Event> findEventByUserWithSingleDate(User user, LocalDate date) {
		return eventRepository.findByAvailableUsersAndDateOrDatePacking(user.getId(),date);

	}
}
