package com.tekcit.festival;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@Slf4j
@EnableJpaAuditing
public class FestivalServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FestivalServiceApplication.class, args);
	}

}
