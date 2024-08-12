package pl.amerevent.amer.model.dto;

import lombok.Data;
import pl.amerevent.amer.criteria.BaseSearchCriteria;

@Data
public class WorkDayListDto extends BaseSearchCriteria {
	private TwoCalendars twoCalendars;
}
