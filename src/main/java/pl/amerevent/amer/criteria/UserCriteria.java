package pl.amerevent.amer.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import pl.amerevent.amer.model.Event;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserCriteria extends BaseSearchCriteria {
	@Id
	private String id;
	private String name;
	private boolean isDriver;
	private List<String> availableDates;
	private Event upcomingEvent;
	private List<Event> eventsToConfirm;

}
