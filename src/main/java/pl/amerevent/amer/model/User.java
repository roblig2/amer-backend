package pl.amerevent.amer.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	private String firstName;
	private String lastName;

	@NotNull
	private Boolean isDriver;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	private Set<UserDate> availableDates;

	private String phoneNumber;
	private LocalDate dateOfBirth;

	@OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_CREDENTIAL_ID", referencedColumnName = "ID")
//	@JsonIgnore
	@JsonManagedReference
	private UserCredential userCredential;

	@ManyToMany(mappedBy = "availableUsers", fetch = FetchType.LAZY)
	@JsonIgnore
	@JsonBackReference
	private Set<Event> eventsAvailable = new HashSet<>();

	@ManyToMany(mappedBy = "availablePackingUsers", fetch = FetchType.LAZY)
	@JsonBackReference

	private Set<Event> eventsAvailablePacking = new HashSet<>();

	@ManyToMany(mappedBy = "blackListedUsers", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Event> eventsBlackListed = new HashSet<>();

	@ManyToMany(mappedBy = "confirmedUsers", fetch = FetchType.LAZY)
	@JsonBackReference
	private Set<Event> eventsConfirmed = new HashSet<>();

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




