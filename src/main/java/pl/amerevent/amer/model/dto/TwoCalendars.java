package pl.amerevent.amer.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pl.amerevent.amer.criteria.BaseSearchCriteria;

import java.time.LocalDate;

@Data
public class TwoCalendars extends BaseSearchCriteria {
	private LocalDate calendarFrom;
	private LocalDate calendarTo;
}
