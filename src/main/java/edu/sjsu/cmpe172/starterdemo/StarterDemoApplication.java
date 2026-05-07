package edu.sjsu.cmpe172.starterdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StarterDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarterDemoApplication.class, args);
	}

}
