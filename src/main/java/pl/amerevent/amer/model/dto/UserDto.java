package pl.amerevent.amer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.amerevent.amer.model.UserDate;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
		private String id;
		private String firstName;
		private String lastName;
		private String username;
		private Boolean isDriver;
		private String driverInfo;
		private Set<UserDate> availableDates;
		private String phoneNumber;
		private LocalDate dateOfBirth;
		private List<EventDto> events;

}
