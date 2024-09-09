package pl.amerevent.amer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;
import pl.amerevent.amer.config.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;

	@Column(name = "NAME")
	private String name;
	@Column(name = "LOCATION")
	private String location;
	@Column(name = "DATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate date;
	@Column(name = "DATE_PACKING")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate datePacking;
	@Column(name = "REQUIRED_USERS")
	private int requiredUsers;
	@Column(name = "REQUIRED_DRIVERS")
	private int requiredDrivers;

	@ManyToMany(fetch =FetchType.LAZY)
	@JoinTable(
			name = "event_available_users",
			joinColumns = @JoinColumn(name = "event_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@JsonManagedReference
	private Set<User> availableUsers = new HashSet<>();

	@ManyToMany(fetch =FetchType.LAZY)
	@JoinTable(
			name = "event_available_packing_users",
			joinColumns = @JoinColumn(name = "event_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@JsonManagedReference
	private Set<User> availablePackingUsers = new HashSet<>();

	@ManyToMany(fetch =FetchType.LAZY)
	@JoinTable(
			name = "event_blacklisted_users",
			joinColumns = @JoinColumn(name = "event_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@JsonManagedReference
	private Set<User> blackListedUsers = new HashSet<>();

	@ManyToMany(fetch =FetchType.LAZY)
	@JoinTable(
			name = "event_confirmed_users",
			joinColumns = @JoinColumn(name = "event_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	@JsonManagedReference
	private Set<User> confirmedUsers = new HashSet<>();
	@Column(name = "IS_MISSING_PEOPLE")
	private Boolean isMissingPeople;
	@Column(name = "EVENT_TIME")
	private String eventTime;
	@Column(name = "PACKING_TIME")
	private String packingTime;
}
