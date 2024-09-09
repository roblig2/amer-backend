package pl.amerevent.amer.criteria;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Sort;

@Data
@EqualsAndHashCode()
public abstract class BaseSearchCriteria {
	private String sortBy;
	private Sort.Direction sortOrder =Sort.Direction.ASC;
	private Integer size =10;
	private Integer page =0;
}
