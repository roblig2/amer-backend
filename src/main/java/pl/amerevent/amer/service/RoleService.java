package pl.amerevent.amer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.amerevent.amer.model.ERole;
import pl.amerevent.amer.model.Role;
import pl.amerevent.amer.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService {
	private final RoleRepository roleRepository;

	public Set<Role> getRoles(List<ERole> roles){
		return roleRepository.findAllByNameIn(roles);
	}
}
