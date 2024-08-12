package pl.amerevent.amer.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "event_users")
public class EventUser {

	@Id
	private String id;
	private String eventId;
	private String userId;
	private boolean confirmed;
}
