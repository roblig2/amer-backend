package pl.amerevent.amer.model.dto;

import lombok.Data;
import pl.amerevent.amer.criteria.BaseSearchCriteria;


@Data
public class UserSearchRequest extends BaseSearchCriteria {

		private String firstName;
		private String lastName;
		private String username;
		private Boolean isDriver;
		private TwoCalendars twoCalendars;


}
