package pl.amerevent.amer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloWorld {
	@GetMapping("/")
	public String hello(){
		return "Hello";
	}
}
