package pl.amerevent.amer.repository;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import pl.amerevent.amer.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {

	public static Specification<User> findByCriteria(String firstName, String lastName, String username) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			// Filtr dla firstName
			if (firstName != null && !firstName.trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
			}

			// Filtr dla lastName
			if (lastName != null && !lastName.trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
			}

			// Filtr dla username (zagnieżdżona relacja userCredential)
			if (username != null && !username.trim().isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("userCredential").get("username")), "%" + username.toLowerCase() + "%"));
			}

			// Zwrócenie predykatów jako warunków zapytania
			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}
