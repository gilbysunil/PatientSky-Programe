package com.eg.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.micrometer.common.util.StringUtils;

@Configuration
public class PatientSkyAppConfig {
	
	@Autowired
	private Environment appProps;
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		String[] allowedOrigins;
		String[] allowedHeaders;
		String[] allowedMethods;
		boolean allowCred = Boolean.parseBoolean(appProps.getProperty("security.cors.allowCredentials"));
		long maxAge = Long.parseLong(appProps.getProperty("security.cors.maxAge"));
		if(StringUtils.isNotBlank(appProps.getProperty("security.cors.allowedOrigins"))) {
			allowedOrigins = appProps.getProperty("security.cors.allowedOrigins").split(",");
		}else {
			allowedOrigins = new String[] {"*"};
		}
		
		if(StringUtils.isNotBlank(appProps.getProperty("security.cors.allowedHeaders"))) {
			allowedHeaders = appProps.getProperty("security.cors.allowedHeaders").split(",");
		}else {
			allowedHeaders = new String[] {"*"};
		}
		
		if(StringUtils.isNotBlank(appProps.getProperty("security.cors.allowedMethods"))) {
			allowedMethods = appProps.getProperty("security.cors.allowedMethods").split(",");
		}else {
			allowedMethods = new String[] {"*"};
		}
		
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(allowedOrigins).allowCredentials(allowCred)
				.allowedHeaders(allowedHeaders).allowedMethods(allowedMethods).maxAge(maxAge);
			}
		};
	}

}
