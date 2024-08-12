package pl.amerevent.amer.model;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RoleDeserializer.class)
public enum ERole {
	ROLE_USER,
	ROLE_MODERATOR,
	ROLE_ADMIN;


}
