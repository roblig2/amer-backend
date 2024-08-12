package pl.amerevent.amer.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Document(collection = "users")
@Builder
public class User {
	@Id
	private String id;

	private String firstName;
	private String lastName;
	@Indexed(unique = true)
	@NotBlank
	@Size(max = 50)
	@Email
	private String username;

	@NotBlank
	@Size(max = 120)
	private String password;

	@NotBlank
	private Boolean isDriver;
	private Set<UserDate> availableDates;
	private String phoneNumber;
	private LocalDate dateOfBirth;

	@DBRef
	private Set<Role> roles = new HashSet<>();


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

}




