package pl.amerevent.amer.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateUserDto extends UserDto {
	private String password;
	private List<String> roles;

}
