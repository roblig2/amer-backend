//package pl.amerevent.amer.repository;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//import pl.amerevent.amer.model.User;
//import pl.amerevent.amer.model.UserDate;
//import pl.amerevent.amer.model.dto.TwoCalendars;
//import pl.amerevent.amer.model.dto.UserSearchRequest;
//import pl.amerevent.amer.model.dto.WorkDayListDto;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
//import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
//import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
//import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Repository
//@RequiredArgsConstructor
//public class UserRepositoryCustomImpl implements UserRepositoryCustom{
//	@Autowired
//	private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
//
////	@Override
////	public Page<User> findUsersByCriteria(UserSearchRequest userSearchRequest) {
////		// Pobierz tabelę
////		DynamoDbTable<User> userTable = dynamoDbEnhancedClient.table("users", TableSchema.fromBean(User.class));
////
////		// Budowanie zapytania Scan
////		ScanEnhancedRequest.Builder scanRequestBuilder = ScanEnhancedRequest.builder()
////				.limit(userSearchRequest.getSize()); // Ustawienie limitu dla paginacji
////
////		// Wykonanie operacji Scan
////		PageIterable<User> scanResults = userTable.scan(scanRequestBuilder.build());
////
////		// Przetwarzanie wyników i ręczne filtrowanie po stronie aplikacji
////		List<User> filteredUsers = scanResults.stream()
////				.flatMap(page -> page.items().stream())
////				.filter(user -> applyFilters(user, userSearchRequest))  // Filtrowanie po stronie aplikacji
////				.peek(user -> user.setPassword(null))  // Ukrywanie hasła
////				.collect(Collectors.toList());
////
////		// Paginacja ręczna po stronie aplikacji
////		int start = userSearchRequest.getPage() * userSearchRequest.getSize();
////		int end = Math.min(start + userSearchRequest.getSize(), filteredUsers.size());
////
////		List<User> paginatedUsers = filteredUsers.subList(start, end);
////
////		// Tworzenie PageImpl
////		Pageable pageable = PageRequest.of(userSearchRequest.getPage(), userSearchRequest.getSize());
////		return new PageImpl<>(paginatedUsers, pageable, filteredUsers.size());
////	}
//
//	private boolean applyFilters(User user, UserSearchRequest request) {
//		// Filtrowanie po imieniu
//		if (StringUtils.hasText(request.getFirstName()) && !user.getFirstName().toLowerCase().contains(request.getFirstName().toLowerCase())) {
//			return false;
//		}
//
//		// Filtrowanie po nazwisku
//		if (StringUtils.hasText(request.getLastName()) && !user.getLastName().toLowerCase().contains(request.getLastName().toLowerCase())) {
//			return false;
//		}
//
//		// Filtrowanie po nazwie użytkownika
//		if (StringUtils.hasText(request.getUsername()) && !user.getUsername().toLowerCase().contains(request.getUsername().toLowerCase())) {
//			return false;
//		}
//
//		// Filtrowanie po roli kierowcy
//		if (request.getIsDriver() != null && !Objects.equals(user.getIsDriver(), request.getIsDriver())) {
//			return false;
//		}
//
//		// Filtrowanie po dostępnych datach
//		if (request.getTwoCalendars().getCalendarFrom() != null && request.getTwoCalendars().getCalendarTo() != null) {
//			LocalDate fromDate = request.getTwoCalendars().getCalendarFrom();
//			LocalDate toDate = request.getTwoCalendars().getCalendarTo();
//
//			if (user.getAvailableDates().stream().noneMatch(date -> !date.getDate().isBefore(fromDate) && !date.getDate().isAfter(toDate))) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
////	@Override
////	public Page<UserDate> findAvailableDatesByUsernameAndDateRange(String username, WorkDayListDto workDayListDto) {
////		// Pobierz tabelę
////		DynamoDbTable<User> userTable = dynamoDbEnhancedClient.table("users", TableSchema.fromBean(User.class));
////
////		// Tworzenie zapytania Scan
////		ScanEnhancedRequest.Builder scanRequestBuilder = ScanEnhancedRequest.builder()
////				.limit(workDayListDto.getSize()); // Ustawienie limitu dla paginacji
////
////		// Wykonanie operacji Scan na tabeli
////		PageIterable<User> scanResults = userTable.scan(scanRequestBuilder.build());
////
////		// Filtrowanie użytkowników po username i zakresach dat po stronie aplikacji
////		List<UserDate> filteredDates = new ArrayList<>();
////
////		scanResults.stream()
////				.flatMap(page -> page.items().stream())
////				.filter(user -> username.equals(user.getUsername()))  // Filtrowanie po nazwie użytkownika
////				.forEach(user -> {
////					// Filtrowanie dostępnych dat użytkownika
////					filteredDates.addAll(user.getAvailableDates().stream()
////							.filter(date -> (workDayListDto.getTwoCalendars().getCalendarFrom() == null || !date.getDate().isBefore(workDayListDto.getTwoCalendars().getCalendarFrom())) &&
////									(workDayListDto.getTwoCalendars().getCalendarTo() == null || !date.getDate().isAfter(workDayListDto.getTwoCalendars().getCalendarTo())))
////							.sorted(getComparator(workDayListDto.getTwoCalendars()))
////							.toList());
////				});
////
////		// Paginacja po stronie aplikacji
////		int start = workDayListDto.getPage() * workDayListDto.getSize();
////		int end = Math.min(start + workDayListDto.getSize(), filteredDates.size());
////
////		if (start >= filteredDates.size()) {
////			return Page.empty(PageRequest.of(workDayListDto.getPage(), workDayListDto.getSize()));
////		}
////
////		List<UserDate> pagedDates = filteredDates.subList(start, end);
////
////		// Tworzenie PageImpl
////		Pageable pageable = PageRequest.of(workDayListDto.getPage(), workDayListDto.getSize());
////		return new PageImpl<>(pagedDates, pageable, filteredDates.size());
////	}
////
////	private Comparator<UserDate> getComparator(TwoCalendars twoCalendars) {
////		return Comparator.comparing(UserDate::getDate);  // W zależności od potrzeb możesz zmodyfikować to sortowanie
////	}
//
////	@Override
////	public List<User> findAvailableUsersByDateAndEventExclusion(LocalDate date, Set<String> eventUserIds) {
////		// Pobranie tabeli użytkowników z DynamoDB
////		DynamoDbTable<User> userTable = dynamoDbEnhancedClient.table("users", TableSchema.fromBean(User.class));
////
////		// Wykonanie operacji Scan
////		ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder().build();
////		PageIterable<User> scanResults = userTable.scan(scanRequest);
////
////		// Filtrowanie wyników po stronie aplikacji
////
////		return scanResults.stream()
////				.flatMap(page -> page.items().stream())
////				.filter(user ->
////						// Filtracja dostępnych dat użytkownika
////						user.getAvailableDates().stream().anyMatch(userDate -> userDate.getDate().equals(date))
////								&&
////								// Wykluczenie użytkowników powiązanych z wydarzeniami
////								!eventUserIds.contains(user.getId())
////				)
////				.collect(Collectors.toList());
////	}
//}
