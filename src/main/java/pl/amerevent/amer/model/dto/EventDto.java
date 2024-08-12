package pl.amerevent.amer.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
	private String name;
	private LocalDate date;
	private Boolean isEvent;
}
