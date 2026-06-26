package com.group2.web_tmdt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WebTmdtApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebTmdtApplication.class, args);
	}

}