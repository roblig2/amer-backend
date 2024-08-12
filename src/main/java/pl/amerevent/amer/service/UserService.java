package pl.amerevent.amer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.amerevent.amer.model.*;
import pl.amerevent.amer.model.dto.*;
import pl.amerevent.amer.repository.UserRepository;
import pl.amerevent.amer.utils.DateRangeUtil;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final EventUserService eventUserService;
	private final EventService eventService;
	private final RoleService roleService;


	public Page<User> getUsers(UserSearchRequest userSearchRequest) {
		return userRepository.findUsersByCriteria(userSearchRequest);
	}

	@Transactional
	public ResponseEntity<User> addWorkDays(WorkDayDto workday) {
		Optional<User> userOpt = eventUserService.findDataBaseUser();
		if(userOpt.isPresent()){
			User user = userOpt.get();
			if(Objects.isNull(user.getAvailableDates())){
				user.setAvailableDates(new HashSet<>());
			}
			if (!workday.isHasRemarks()) {
				List<LocalDate> dates = DateRangeUtil.generateDateRange(workday.getTwoCalendars().getCalendarFrom(), workday.getTwoCalendars().getCalendarTo());
				ArrayList<UserDate> userDates = new ArrayList<>();
				dates.forEach(date ->
						userDates.add(new UserDate(date))
				);
				user.getAvailableDates().addAll(userDates);
			}else{
				user.getAvailableDates().add(new UserDate(workday.getDate(),workday.getRemark()));
			}
			User saved = userRepository.save(user);
			return new ResponseEntity<>(saved, HttpStatus.CREATED);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}



	public Page<UserDateDto> getWorkDays(WorkDayListDto workDayListDto) {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Page<UserDate> availableDates = userRepository.findAvailableDatesByUsernameAndDateRange(username, workDayListDto);

		List<UserDateDto> userDateDtos = availableDates.getContent().stream()
				.map(date -> new UserDateDto(date.getDate(), Objects.nonNull(date.getRemark()) ? date.getRemark() : ""))
				.collect(Collectors.toList());

		return new PageImpl<>(userDateDtos, availableDates.getPageable(), availableDates.getTotalElements());
	}

	@Transactional
	public List<UserDto> getAvailableUsers(LocalDate date) {
		EventSearchRequest eventSearchRequest = new EventSearchRequest();
		eventSearchRequest.setPage(0);
		eventSearchRequest.setSize(9999999);
		TwoCalendars twoCalendars = new TwoCalendars();
		twoCalendars.setCalendarFrom(date);
		twoCalendars.setCalendarTo(date);

		eventSearchRequest.setTwoCalendars(twoCalendars);
		Page<Event> events = eventService.getAllEvents(eventSearchRequest);
		List<EventDto> eventName = new ArrayList<>();
		Set<String> userIds = new HashSet<>();
		events.forEach(event -> {
			boolean isEvent = false;
			if (Objects.nonNull(event.getAvailableUsers())) {
				isEvent =event.getAvailableUsers().stream().map(User::getId).findFirst().isPresent();
				userIds.addAll(event.getAvailableUsers().stream().map(User::getId).collect(Collectors.toSet()));
			}
			if (Objects.nonNull(event.getAvailablePackingUsers())) {
				userIds.addAll(event.getAvailablePackingUsers().stream().map(User::getId).collect(Collectors.toSet()));
			}
			eventName.add(new EventDto(event.getName(), isEvent ? event.getDate() : event.getDatePacking(), isEvent));
		});
		List<User> users = userRepository.findAvailableUsersByDateAndEventExclusion(date, userIds);
		ArrayList<UserDto> userDtos = new ArrayList<>();
		users.forEach(user -> userDtos.add(UserDto.builder()
				.dateOfBirth(user.getDateOfBirth())
				.availableDates(user.getAvailableDates().stream().filter(userDate -> userDate.getDate().isEqual(date)).collect(Collectors.toSet()))
				.phoneNumber(user.getPhoneNumber())
				.lastName(user.getLastName())
				.username(user.getUsername())
				.firstName(user.getFirstName())
				.isDriver(user.getIsDriver())
				.events(eventName)
				.id(user.getId()).build()));
		return userDtos;
	}

	public Optional<User> findUserByUserName(String username) {
		return userRepository.findByUsername(username);
	}

	public void addUser(User user) {
		userRepository.save(user);
	}

	@Transactional
	public ResponseEntity editUser(CreateUserDto userDto) {
		if (Objects.nonNull(userDto.getUsername())) {
			Optional<User> userOpt = findUserByUserName(userDto.getUsername());

			ArrayList<ERole> eRoles = new ArrayList<>();
			if (Objects.nonNull(userDto.getRoles())) {
				userDto.getRoles().forEach(role -> eRoles.add(ERole.valueOf(role)));
			}
			Set<Role> roles = roleService.getRoles(eRoles);

			if (userOpt.isEmpty()) {
				return ResponseEntity.badRequest().build();
			}
			else if(Objects.nonNull(roles) && !roles.isEmpty()){
				User user = userOpt.get();
				List<Field> dtoFields = getAllFields(new ArrayList<>(), userDto.getClass());
				List<Field> userFields = getAllFields(new ArrayList<>(), user.getClass());

				for (Field dtoField : dtoFields) {
					dtoField.setAccessible(true);

					try {
						Object value = dtoField.get(userDto);

						if (value != null && !(value instanceof String && ((String) value).isEmpty())) {
							for (Field userField : userFields) {
								userField.setAccessible(true);
								if (userField.getName().equals(dtoField.getName()) &&
										userField.getType().equals(dtoField.getType())) {
									if (dtoField.getName().equals("password")) {
										assert value instanceof String;
										userField.set(user, passwordEncoder.encode((String) value));
									} else {
										userField.set(user, value);
									}
								}
							}
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
					}
				}
				userRepository.save(user);
				return ResponseEntity.ok().build();
			}
		}
		return ResponseEntity.badRequest().build();

	}

	@Transactional
	public ResponseEntity<ResponseMessage> changePassword(PasswordDto password) {
		Optional<User> userOpt = getCurrentUser();
		ResponseMessage responseMessage = new ResponseMessage();
		if(userOpt.isPresent() && passwordEncoder.matches(password.getCurrentPassword(),userOpt.get().getPassword())){
			User user = userOpt.get();
			if (password.getNewPassword().equals(password.getConfirmPassword())) {
				user.setPassword(passwordEncoder.encode(password.getNewPassword()));
				userRepository.save(user);
				responseMessage.setMessage("zaktualizowano hasło");
				return new ResponseEntity<>(responseMessage,HttpStatus.OK);
			}
			else {
				responseMessage.setMessage("Hasła muszą być takie same");
				return new ResponseEntity<>(responseMessage,HttpStatus.BAD_REQUEST);
			}
		}
		responseMessage.setMessage("Podane hasło jest nieprawidłowe");
		return new ResponseEntity<>(responseMessage,HttpStatus.BAD_REQUEST);
	}

	private Optional<User> getCurrentUser() {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(username);
	}

	public ResponseEntity<User> getUserDetails(String id) {
		Optional<User> userOpt = userRepository.findById(id);
		return userOpt.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	public ResponseEntity<Object> deleteByDate(LocalDate date) {
		Optional<User> userOpt = getCurrentUser();
		if (userOpt.isPresent()) {
			User user = userOpt.get();

			Set<UserDate> userDates = user.getAvailableDates().stream().filter(savedDates -> !savedDates.getDate().isEqual(date)).collect(Collectors.toSet());
			if (user.getAvailableDates().size() != userDates.size()) {
				user.setAvailableDates(userDates);
				List<Event> events = eventUserService.findEventByUserWithSingleDate(user, date);
				deleteUserFromEventAndAddToBlacklist(user, events);
				userRepository.save(user);
				return new ResponseEntity<>(HttpStatus.CREATED);
			}
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	private void deleteUserFromEventAndAddToBlacklist(User user, List<Event> events) {
		events.forEach(event -> {
					if (Objects.nonNull(event.getAvailableUsers())) {
						event.getAvailableUsers().remove(user);
					}
					if (Objects.nonNull(event.getAvailablePackingUsers())) {
						event.getAvailablePackingUsers().remove(user);
					}
					if (Objects.nonNull(event.getConfirmedUsers())) {
						event.getConfirmedUsers().remove(user);
					}
					if (Objects.isNull(event.getBlackListedUsers())) {
						event.setBlackListedUsers(new HashSet<>());
					}
					event.getBlackListedUsers().add(user);
					eventService.updateEvent(event);
				}
		);

	}

	private List<Field> getAllFields(List<Field> fields, Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			fields.add(field);
		}

		if (type.getSuperclass() != null) {
			getAllFields(fields, type.getSuperclass());
		}

		return fields;
	}

	@Transactional
	public ResponseEntity<ResponseMessage> deleteUser(String id) {
		ResponseMessage responseMessage = new ResponseMessage();
		Optional<User> userOpt = userRepository.findById(id);
		userOpt.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT));
		userOpt.ifPresent(user->{
			userRepository.delete(user);
			responseMessage.setMessage("Usunięto użytkownika " + user.getFirstName() + " " + user.getLastName());

		});
		if (Objects.nonNull(responseMessage.getMessage())) {
			return new ResponseEntity<>(responseMessage, HttpStatus.OK);
		}else{
			responseMessage.setMessage("Wystąpił problem przy usuwaniu użytkownika");
			return new ResponseEntity<>(responseMessage,HttpStatus.BAD_REQUEST);
		}

	}
}
