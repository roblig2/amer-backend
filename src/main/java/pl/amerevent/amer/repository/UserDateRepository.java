package pl.amerevent.amer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.amerevent.amer.model.UserDate;

import java.util.UUID;

public interface UserDateRepository extends JpaRepository<UserDate, UUID> {
}
