package pl.amerevent.amer.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DateRangeUtil {

	public static List<LocalDate> generateDateRange(LocalDate from, LocalDate to) {
		List<LocalDate> dates = new ArrayList<>();
		if (from != null && to != null && !from.isAfter(to)) {
			LocalDate current = from;
			while (!current.isAfter(to)) {
				dates.add(current);
				current = current.plusDays(1);
			}
		}
		return dates;
	}
}
