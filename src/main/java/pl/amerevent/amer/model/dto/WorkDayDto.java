package pl.amerevent.amer.model.dto;

import lombok.Data;
import pl.amerevent.amer.criteria.BaseSearchCriteria;

import java.time.LocalDate;

@Data
public class WorkDayDto  {
	private TwoCalendars twoCalendars;
	private LocalDate date;
	private boolean hasRemarks;
	private String remark;

}
