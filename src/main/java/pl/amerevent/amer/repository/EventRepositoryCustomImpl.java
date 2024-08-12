package pl.amerevent.amer.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pl.amerevent.amer.model.Event;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.dto.EventSearchRequest;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {


	private final MongoTemplate mongoTemplate;

	@Override
	public Page<Event> findAllEvents(EventSearchRequest eventSearchRequest) {
		Query query = createQuery(eventSearchRequest);
		return prepareQuery(eventSearchRequest, query);
	}

	private static Query createQuery(EventSearchRequest eventSearchRequest) {
		Query query = new Query();

		List<Criteria> criteriaList = new ArrayList<>();
		// Poprawka: Połączenie gte i lte w jednym Criteria
		if (eventSearchRequest.getTwoCalendars().getCalendarFrom() != null && eventSearchRequest.getTwoCalendars().getCalendarTo() != null) {
			query.addCriteria(Criteria.where("date").gte(eventSearchRequest.getTwoCalendars().getCalendarFrom()).lte(eventSearchRequest.getTwoCalendars().getCalendarTo()));
		} else if (eventSearchRequest.getTwoCalendars().getCalendarFrom() != null) {
			query.addCriteria(Criteria.where("date").gte(eventSearchRequest.getTwoCalendars().getCalendarFrom()));
		} else if (eventSearchRequest.getTwoCalendars().getCalendarTo() != null) {
			query.addCriteria(Criteria.where("date").lte(eventSearchRequest.getTwoCalendars().getCalendarTo()));
		}

		if (eventSearchRequest.getIsMissingPeople() != null) {
			if (eventSearchRequest.getIsMissingPeople()) {
				query.addCriteria(Criteria.where("isMissingPeople").is(true));
			}
		}
		if (StringUtils.hasText(eventSearchRequest.getName())) {
			query.addCriteria(Criteria.where("name").regex(eventSearchRequest.getName(), "i"));
		}
		if (StringUtils.hasText(eventSearchRequest.getLocation())) {
			query.addCriteria(Criteria.where("location").regex(eventSearchRequest.getLocation(), "i"));
		}
		if (StringUtils.hasText(eventSearchRequest.getSortBy()) && Objects.nonNull(eventSearchRequest.getSortOrder())) {
			query.with(Sort.by(eventSearchRequest.getSortOrder(), eventSearchRequest.getSortBy()));
		}

		if (!criteriaList.isEmpty()) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		return query;
	}

	@Override
	public Page<Event> findEventsByUser(EventSearchRequest eventSearchRequest, String id) {
		Query query = createQuery(eventSearchRequest);
		if (StringUtils.hasText(id)) {
			query.addCriteria(new Criteria().orOperator(
					Criteria.where("availableUsers.id").is(id),
					Criteria.where("availablePackingUsers.id").is(id)
			));
		}
		return prepareQuery(eventSearchRequest, query);
	}

	@Override
	public List<Event> findByAvailableUsersAndDateOrDatePacking(User user, LocalDate date) {
		Query query = new Query();

		if (Objects.nonNull(user.getId()) && Objects.nonNull(date)) {
			query.addCriteria(new Criteria().orOperator(
					new Criteria().andOperator(
							Criteria.where("availablePackingUsers.id").is(user.getId()),
							Criteria.where("datePacking").is(date)
					),
					new Criteria().andOperator(
							Criteria.where("availableUsers.id").is(user.getId()),
							Criteria.where("date").is(date)
					)
			));
		}

		return mongoTemplate.find(query, Event.class);
	}

	private Page<Event> prepareQuery(EventSearchRequest eventSearchRequest, Query query) {
		Pageable pageable = PageRequest.of(eventSearchRequest.getPage(), eventSearchRequest.getSize());
		long total = mongoTemplate.count(query, Event.class);
		List<Event> events = mongoTemplate.find(query.with(pageable), Event.class);
		events.forEach(event -> {
			event.setAvailableUsers(Objects.nonNull(event.getAvailableUsers()) ? event.getAvailableUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
			event.setConfirmedUsers(Objects.nonNull(event.getConfirmedUsers()) ? event.getConfirmedUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
			event.setBlackListedUsers(Objects.nonNull(event.getBlackListedUsers()) ? event.getBlackListedUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
			event.setAvailablePackingUsers(Objects.nonNull(event.getAvailablePackingUsers()) ? event.getAvailablePackingUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
		});

		return new PageImpl<>(events, pageable, total);
	}
}
