package pl.amerevent.amer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.User;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EventRepository extends EventRepositoryCustom ,MongoRepository<Event, String> {



}
