package pl.amerevent.amer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.amerevent.amer.model.*;
import pl.amerevent.amer.model.dto.*;
import pl.amerevent.amer.service.EventUserService;
import pl.amerevent.amer.service.RoleService;
import pl.amerevent.amer.service.UserService;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final PasswordEncoder passwordEncoder;
	private final UserService userService;
	private final EventUserService eventUserService;
	private final RoleService roleService;

	@GetMapping("{id}")
	public ResponseEntity<UserDto> getUserDetails(@PathVariable UUID id) {
		return userService.getUserDetails(id);
	}

	@GetMapping("/edit/{id}")
	public ResponseEntity<UserDto> getUserDetailsForEdit(@PathVariable UUID id) {
		return userService.getUserDetails(id);
	}
	@GetMapping
	public ResponseEntity<User> getSelfUser() {
		Optional<User> dataBaseUser = eventUserService.findDataBaseUser();
		return dataBaseUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
	}

	@PostMapping("/create")
	public ResponseEntity createUser(@RequestBody CreateUserDto userDto){
		if (Objects.nonNull(userDto.getUsername())) {
			Optional<User> dataBaseUser = userService.findUserByUserName(userDto.getUsername());

			ArrayList<ERole> eRoles = new ArrayList<>();
			if (Objects.nonNull(userDto.getRoles())) {
				userDto.getRoles().forEach(role -> eRoles.add(ERole.valueOf(role)));
			}
			Set<Role> roles = roleService.getRoles(eRoles);

			if (dataBaseUser.isPresent()) {
				return ResponseEntity.badRequest().build();
			}
				UserCredential userCredential = new UserCredential();
				userCredential.setUsername(userDto.getUsername());
				userCredential.setPassword(passwordEncoder.encode(userDto.getPassword()));
				userCredential.setRoles(roles);
				User user = User.builder()
						.userCredential(userCredential)
						.lastName(userDto.getLastName())
						.firstName(userDto.getFirstName())
						.phoneNumber(userDto.getPhoneNumber())
						.dateOfBirth(userDto.getDateOfBirth())
						.isDriver(userDto.getIsDriver())
						.build();

				userService.addUser(user);
				return ResponseEntity.ok().build();

		}
		return ResponseEntity.badRequest().build();
	}

	@PutMapping("/update")
	public ResponseEntity updateUser(@RequestBody CreateUserDto userDto){
		return userService.editUser(userDto);
	}

	@PutMapping("/changePassword")
	public ResponseEntity<ResponseMessage> changePassword(@RequestBody PasswordDto password) {
		return userService.changePassword(password);
	}

	@PostMapping
	public Page<UserDto> getUsers(@RequestBody UserSearchRequest userSearchRequest) {
		return userService.getUsers(userSearchRequest);
	}

	@PostMapping("/availableUsers")
	public List<UserDto> getAvailableUsers(@RequestBody LocalDate date) {
		return userService.getAvailableUsers(date);
	}
	@DeleteMapping("/availableUsers/{date}")
	public ResponseEntity<Object> deleteUserAvailability(@PathVariable LocalDate date) {
		return userService.deleteByDate(date);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseMessage> deleteUser(@PathVariable UUID id) {
		return userService.deleteUser(id);
	}
}
