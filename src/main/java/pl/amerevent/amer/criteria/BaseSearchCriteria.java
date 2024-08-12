package pl.amerevent.amer.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Sort;

@Data
@EqualsAndHashCode()
public abstract class BaseSearchCriteria {
	private String sortBy;
	private Sort.Direction sortOrder;
	private Integer size;
	private Integer page;
}
