//package pl.amerevent.amer.repository;
//
//import lombok.RequiredArgsConstructor;
//import org.socialsignin.spring.data.dynamodb.repository.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//import pl.amerevent.amer.model.Event;
//import pl.amerevent.amer.model.User;
//import pl.amerevent.amer.model.dto.EventSearchRequest;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
//import software.amazon.awssdk.enhanced.dynamodb.Key;
//import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
//import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
//import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
//import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
//import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//public class EventRepositoryCustomImpl implements EventRepositoryCustom {
//
//
//	private final MongoTemplate mongoTemplate;
//
//
//
//
//
//
//
//	private Page<Event> prepareQuery(EventSearchRequest eventSearchRequest, Query query) {
//		Pageable pageable = PageRequest.of(eventSearchRequest.getPage(), eventSearchRequest.getSize());
//		long total = mongoTemplate.count(query, Event.class);
//		List<Event> events = mongoTemplate.find(query.with(pageable), Event.class);
//		events.forEach(event -> {
//			event.setAvailableUsers(Objects.nonNull(event.getAvailableUsers()) ? event.getAvailableUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
//			event.setConfirmedUsers(Objects.nonNull(event.getConfirmedUsers()) ? event.getConfirmedUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
//			event.setBlackListedUsers(Objects.nonNull(event.getBlackListedUsers()) ? event.getBlackListedUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
//			event.setAvailablePackingUsers(Objects.nonNull(event.getAvailablePackingUsers()) ? event.getAvailablePackingUsers().stream().peek(user -> user.setPassword(null)).collect(Collectors.toSet()) : null);
//		});
//
//		return new PageImpl<>(events, pageable, total);
//	}
//}
