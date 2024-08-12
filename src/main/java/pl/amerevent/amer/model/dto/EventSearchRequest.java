package pl.amerevent.amer.model.dto;


import lombok.Data;
import pl.amerevent.amer.criteria.BaseSearchCriteria;

@Data
public class EventSearchRequest extends BaseSearchCriteria {
	private TwoCalendars twoCalendars;
	private Boolean isMissingPeople;
	private String name;
	private String location;
}
