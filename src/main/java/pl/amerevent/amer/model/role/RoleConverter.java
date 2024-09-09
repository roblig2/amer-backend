package pl.amerevent.amer.model.role;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import pl.amerevent.amer.model.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RoleConverter implements DynamoDBTypeConverter<List<Role>, Set<Role>>{

	@Override
	public List<Role> convert(Set<Role> roleSet) {
		return roleSet.stream().toList();
	}

	@Override
	public Set<Role> unconvert(List<Role> roles) {
		return new HashSet<>(roles);
	}
}
