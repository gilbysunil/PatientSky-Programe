package com.eg.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
public class PatientSkyApplication extends SpringBootServletInitializer implements WebApplicationInitializer {
	
	@Override
	public SpringApplicationBuilder configure(SpringApplicationBuilder app) {
		return app.sources(PatientSkyApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(PatientSkyApplication.class, args);
	}

}
