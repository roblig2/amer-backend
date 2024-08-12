package pl.amerevent.amer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDate {
	private LocalDate date;
	private String remark;

	public UserDate(LocalDate date) {
		this.date = date;
	}
}
