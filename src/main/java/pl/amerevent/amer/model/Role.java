package pl.amerevent.amer.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name =  "roles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(name= "NAME")
	@Enumerated(EnumType.STRING)
	private ERole name;
	@ManyToMany(mappedBy = "roles")
	private Set<UserCredential> userCredentials = new HashSet<>();

	public Role(ERole name) {
		this.name = name;
	}
}
