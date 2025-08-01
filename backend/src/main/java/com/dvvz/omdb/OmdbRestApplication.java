package com.dvvz.omdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OmdbRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmdbRestApplication.class, args);
	}

}
