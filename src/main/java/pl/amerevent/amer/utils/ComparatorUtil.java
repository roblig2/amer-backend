package pl.amerevent.amer.utils;

import org.springframework.util.StringUtils;
import pl.amerevent.amer.model.UserDate;
import pl.amerevent.amer.model.dto.TwoCalendars;

import java.util.Comparator;
import java.util.Objects;

public class ComparatorUtil {
	public static Comparator<UserDate> getComparator(TwoCalendars twoCalendars) {
		if (StringUtils.hasText(twoCalendars.getSortBy()) && Objects.nonNull(twoCalendars.getSortOrder())) {
			Comparator<UserDate> comparator;
			switch (twoCalendars.getSortBy()) {
				case "date":
					comparator = Comparator.comparing(UserDate::getDate);
					break;
				case "remark":
					comparator = Comparator.comparing(UserDate::getRemark);
					break;
				default:
					comparator = Comparator.comparing(UserDate::getDate);
					break;
			}

			if (twoCalendars.getSortOrder().isDescending()) {
				comparator = comparator.reversed();
			}

			return comparator;
		}
		return Comparator.comparing(UserDate::getDate); // Default sorting
	}
}
