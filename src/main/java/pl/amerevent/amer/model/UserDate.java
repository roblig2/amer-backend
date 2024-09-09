package pl.amerevent.amer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="USER_DATE")
public class UserDate {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID id;
	private LocalDate date;
	private String remark;
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID")
	private User user;


	public UserDate(LocalDate date,User user) {
		this.date = date;
		this.user = user;
	}
	public UserDate(LocalDate date) {
		this.date = date;
	}
	public UserDate(LocalDate date, String remark,User user) {
		this.date = date;
		this.user = user;
		this.remark = remark;
	}
	public UserDate(LocalDate date, String remark) {
		this.date = date;
		this.remark = remark;
	}
}
