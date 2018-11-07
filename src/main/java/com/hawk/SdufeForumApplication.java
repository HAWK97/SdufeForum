package com.hawk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SdufeForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdufeForumApplication.class, args);
	}
}
