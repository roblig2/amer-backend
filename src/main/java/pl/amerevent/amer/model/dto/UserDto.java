package pl.amerevent.amer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.amerevent.amer.model.Role;
import pl.amerevent.amer.model.UserDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
		private UUID id;
		private String firstName;
		private String lastName;
		private String username;
		private Boolean isDriver;
		private String driverInfo;
		private Set<UserDateDto> availableDates;
		private String phoneNumber;

		private LocalDate dateOfBirth;
		private Set<Role> definedRoles;
//		private String password;
		private List<EventDto> events;

}
