package pl.amerevent.amer.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "roles")
@NoArgsConstructor
@Data
public class Role {
	@Id
	private String id;

	private ERole name;


	public Role(ERole name) {
		this.name = name;
	}
}
