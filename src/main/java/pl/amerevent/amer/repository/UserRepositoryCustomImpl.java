package pl.amerevent.amer.repository;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bson.conversions.Bson;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pl.amerevent.amer.model.User;
import pl.amerevent.amer.model.UserDate;
import pl.amerevent.amer.model.dto.UserSearchRequest;
import pl.amerevent.amer.model.dto.WorkDayListDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static pl.amerevent.amer.utils.ComparatorUtil.getComparator;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom{
	private final MongoTemplate mongoTemplate;

	@Override
	public Page<User> findUsersByCriteria(UserSearchRequest userSearchRequest) {
		Query query = new Query();

		List<Criteria> criteriaList = new ArrayList<>();
		if (StringUtils.hasText(userSearchRequest.getFirstName())) {
			criteriaList.add(Criteria.where("firstName").regex(userSearchRequest.getFirstName(),"i"));
		}

		if (StringUtils.hasText(userSearchRequest.getLastName())) {
			criteriaList.add(Criteria.where("lastName").regex(userSearchRequest.getLastName(),"i"));
		}
		if (StringUtils.hasText(userSearchRequest.getUsername())) {
			criteriaList.add(Criteria.where("username").regex(userSearchRequest.getUsername(),"i"));
		}
		if (userSearchRequest.getIsDriver() != null) {
			if (userSearchRequest.getIsDriver()) {
				query.addCriteria(Criteria.where("isDriver").is(true));
			}
		}
		if (userSearchRequest.getTwoCalendars().getCalendarFrom() != null && userSearchRequest.getTwoCalendars().getCalendarTo() != null) {
			criteriaList.add(Criteria.where("availableDates").elemMatch(
					Criteria.where("$gte").is(userSearchRequest.getTwoCalendars().getCalendarFrom())
							.andOperator(Criteria.where("$lte").is(userSearchRequest.getTwoCalendars().getCalendarTo()))

			));
		}
		if (!criteriaList.isEmpty()) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}
		Pageable pageable = PageRequest.of(userSearchRequest.getPage(), userSearchRequest.getSize());
		long total = mongoTemplate.count(query, User.class);
		List<User> users = mongoTemplate.find(query.with(pageable), User.class).stream().peek(user -> user.setPassword(null)).toList();
		return new PageImpl<>(users, pageable, total);

	}

	@Override
	public Page<UserDate> findAvailableDatesByUsernameAndDateRange(String username, WorkDayListDto workDayListDto) {
		Query query = new Query();
		Pageable pageable = PageRequest.of(workDayListDto.getPage(), workDayListDto.getSize());

		List<Criteria> criteriaList = new ArrayList<>();
		if (StringUtils.hasText(username)) {
			criteriaList.add(Criteria.where("username").is(username));
		}

		if (workDayListDto.getTwoCalendars().getCalendarFrom() != null && workDayListDto.getTwoCalendars().getCalendarTo() != null) {
			criteriaList.add(Criteria.where("availableDates").elemMatch(
					Criteria.where("date").gte(workDayListDto.getTwoCalendars().getCalendarFrom()).lte(workDayListDto.getTwoCalendars().getCalendarTo())
			));
		}
		if (StringUtils.hasText(workDayListDto.getSortBy()) && Objects.nonNull(workDayListDto.getSortOrder())) {
			query.with(Sort.by(workDayListDto.getSortOrder(),workDayListDto.getSortBy()));
		}


		if (!criteriaList.isEmpty()) {
			query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		}

		User user = mongoTemplate.findOne(query, User.class);
		if (user != null) {
			List<UserDate> filteredDates = user.getAvailableDates().stream()
					.filter(date -> (workDayListDto.getTwoCalendars().getCalendarFrom() == null || !date.getDate().isBefore(workDayListDto.getTwoCalendars().getCalendarFrom())) &&
							(workDayListDto.getTwoCalendars().getCalendarTo() == null || !date.getDate().isAfter(workDayListDto.getTwoCalendars().getCalendarTo())))
					.sorted(getComparator(workDayListDto.getTwoCalendars()))
					.toList();

			int start = workDayListDto.getPage() * workDayListDto.getSize();
			if (start > filteredDates.size()) {
				return Page.empty(pageable);
			}
			int end = Math.min((start + workDayListDto.getSize()), filteredDates.size());
			List<UserDate> pagedDates = filteredDates.subList(start, end);

			return new PageImpl<>(pagedDates, pageable, filteredDates.size());
		}

		return Page.empty(pageable);
	}

	@Override
	public List<User> findAvailableUsersByDateAndEventExclusion(LocalDate date, Set<String> eventUserIds) {
		// Tworzenie kryteriów wyszukiwania
		Criteria dateCriteria = Criteria.where("availableDates.date").is(date);
		Criteria exclusionCriteria = Criteria.where("_id").nin(eventUserIds);

		// Tworzenie zapytania
		Query query = new Query();
		query.addCriteria(dateCriteria);
		query.addCriteria(exclusionCriteria);

		// Wykonywanie zapytania i zwracanie wyników
		return mongoTemplate.find(query, User.class);
	}

}
