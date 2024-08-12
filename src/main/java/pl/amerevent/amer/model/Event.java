package pl.amerevent.amer.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "events")
public class Event {
	@Id
	private String id;
	private String name;
	private String location;
	private LocalDate date;
	private LocalDate datePacking;
	private int requiredUsers;
	private int requiredDrivers;
	private Set<User> availableUsers;
	private Set<User> availablePackingUsers;
	private Set<User> blackListedUsers;
	private Set<User> confirmedUsers;
	private Boolean isMissingPeople;
	private String eventTime;
	private String packingTime;
}
