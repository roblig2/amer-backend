package pl.amerevent.amer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.amerevent.amer.model.*;
import pl.amerevent.amer.model.dto.*;
import pl.amerevent.amer.repository.UserCredentialRepository;
import pl.amerevent.amer.repository.UserRepository;
import pl.amerevent.amer.repository.UserSpecifications;
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
	private final UserCredentialRepository userCredentialRepository;
	private final EventUserService eventUserService;
	private final EventService eventService;
	private final RoleService roleService;


	public Page<UserDto> getUsers(UserSearchRequest userSearchRequest) {
		Pageable pageable = PageRequest.of(userSearchRequest.getPage(), userSearchRequest.getSize());

		Page<User> allUsers = userRepository.findAll(UserSpecifications.findByCriteria(userSearchRequest.getFirstName(), userSearchRequest.getLastName(), userSearchRequest.getUsername()), pageable);
		return allUsers.map(user -> UserDto.builder().id(user.getId()).isDriver(user.getIsDriver()).firstName(user.getFirstName()).lastName(user.getLastName()).phoneNumber(user.getPhoneNumber()).username(user.getUserCredential().getUsername()).build());
	}

	@Transactional
	public ResponseEntity addWorkDays(WorkDayDto workday) {
		Optional<User> userOpt = eventUserService.findDataBaseUser();
		if(userOpt.isPresent()){
			User user = userOpt.get();
//			new UserDate())
			if(Objects.isNull(user.getAvailableDates())){
				user.setAvailableDates(new HashSet<>());
			}
			if (!workday.isHasRemarks()) {
				List<LocalDate> dates = DateRangeUtil.generateDateRange(workday.getTwoCalendars().getCalendarFrom(), workday.getTwoCalendars().getCalendarTo());
				dates.forEach(date ->
						eventUserService.addUserDate(new UserDate(date,user))
				);
			}else{
				eventUserService.addUserDate(new UserDate(workday.getDate(), workday.getRemark(),user));
			}
//			UserDate saved = eventUserService.addUserDate()
			return new ResponseEntity<>( HttpStatus.CREATED);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}



	public Page<UserDateDto> getWorkDays(WorkDayListDto workDayListDto) {
		LocalDate calendarFrom = workDayListDto.getTwoCalendars() != null ? workDayListDto.getTwoCalendars().getCalendarFrom() : null;
		LocalDate calendarTo = workDayListDto.getTwoCalendars() != null ? workDayListDto.getTwoCalendars().getCalendarTo() : null;
		Pageable pageable = PageRequest.of(workDayListDto.getPage(), workDayListDto.getSize());
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Page<UserDate> availableDates = userRepository.findAvailableDatesByUsernameAndDateRange(username, calendarFrom, calendarTo, pageable);

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
		eventSearchRequest.setSortBy("date");
		eventSearchRequest.setSortOrder(Sort.Direction.ASC);
		eventSearchRequest.setTwoCalendars(twoCalendars);
		Page<Event> events = eventService.getAllEvents(eventSearchRequest);
		List<EventDto> eventName = new ArrayList<>();
		Set<UUID> userIds = new HashSet<>();
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
		users.forEach(user -> userDtos.add(
				buildUserDto(user)
				.availableDates(user.getAvailableDates().stream().filter(userDate -> userDate.getDate().isEqual(date)).map(userDate -> new UserDateDto(userDate.getDate(),userDate.getRemark())).collect(Collectors.toSet()))
				.events(eventName)
				.build()));
		return userDtos;
	}

	public Optional<User> findUserByUserName(String username) {
		Optional<UserCredential> userCredentialOpt = userCredentialRepository.findByUsername(username);
		return userCredentialOpt.flatMap(userCredential -> userRepository.findByUsername(userCredential.getUsername()));
	}

	@Transactional
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
		Optional<UserCredential> userOpt = getCurrentUserCredentials();
		ResponseMessage responseMessage = new ResponseMessage();
		if(userOpt.isPresent() && passwordEncoder.matches(password.getCurrentPassword(),userOpt.get().getPassword())){
			UserCredential user = userOpt.get();
			if (password.getNewPassword().equals(password.getConfirmPassword())) {
				user.setPassword(passwordEncoder.encode(password.getNewPassword()));
				userCredentialRepository.save(user);
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
		Optional<UserCredential> userCredentialOpt = userCredentialRepository.findByUsername(username);
		return userCredentialOpt.map(userCredential -> userRepository.findByUsername(userCredential.getUsername())).orElse(null);
	}
	private Optional<UserCredential> getCurrentUserCredentials() {
		String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userCredentialRepository.findByUsername(username);
	}
	public ResponseEntity<UserDto> getUserDetails(UUID id) {
		Optional<User> userOpt = userRepository.findById(id);
		return userOpt.map(user -> new ResponseEntity<>(buildUserDto(user).build(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	private UserDto.UserDtoBuilder buildUserDto(User user) {
		return UserDto.builder()
				.dateOfBirth(user.getDateOfBirth())
//				.availableDates(user.getAvailableDates().stream().filter(userDate -> userDate.getDate().isEqual(date)).collect(Collectors.toSet()))
				.phoneNumber(user.getPhoneNumber())
				.lastName(user.getLastName())
				.username(user.getUserCredential().getUsername())
				.definedRoles(user.getUserCredential().getRoles())
				.firstName(user.getFirstName())
				.isDriver(user.getIsDriver())
//				.events(eventName)
				.id(user.getId());
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
	public ResponseEntity<ResponseMessage> deleteUser(UUID id) {
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
