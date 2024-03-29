package com.wxy;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("com.wxy.*")
public class ConsolebusApplication {
	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(ConsolebusApplication.class);
		springApplication.addListeners(new ApplicationStartup());
		springApplication.run(args);
    }
}
