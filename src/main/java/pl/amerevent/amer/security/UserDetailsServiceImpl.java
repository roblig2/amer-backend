package pl.amerevent.amer.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.amerevent.amer.model.*;
import pl.amerevent.amer.repository.RoleRepository;
import pl.amerevent.amer.repository.UserCredentialRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserCredentialRepository userCredentialRepository;
	@Autowired
	private RoleRepository roleRepository;

	public UserDetailsServiceImpl() {
		System.out.println("UserDetailsServiceImpl created");
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserCredential user =	userCredentialRepository.findByUsername(username)
					.orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
		return UserDetailsImpl.build(user);
	}

}
