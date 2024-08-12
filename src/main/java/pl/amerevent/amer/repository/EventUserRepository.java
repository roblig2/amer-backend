package pl.amerevent.amer.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.amerevent.amer.model.EventUser;

import java.util.List;

@Repository
public interface EventUserRepository extends MongoRepository<EventUser, String> {
	List<EventUser> findByEventId(String eventId);
	List<EventUser> findByUserId(String userId);
	EventUser findByEventIdAndUserId(String eventId, String userId);
}
