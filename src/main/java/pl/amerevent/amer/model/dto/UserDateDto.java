package pl.amerevent.amer.model.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDateDto {
	private LocalDate date;
	private String remark;

	public UserDateDto(LocalDate date, String remark) {
		this.date = date;
		this.remark = remark;
	}

}
