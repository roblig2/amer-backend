package pl.amerevent.amer.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class EventCriteria extends BaseSearchCriteria {
	@Id
	private String id;
	private String name;
	private String date;
	private int requiredUsers;
	private int requiredDrivers;
	private List<String> assignedUserIds;

	private boolean missingPeople;

}
