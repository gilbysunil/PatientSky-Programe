package com.eg.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.eg.test.PatientSkyAppInterceptor;

@Component
public class PatientSkyAppServerInterceptorConfig implements WebMvcConfigurer{
	
	@Autowired
	private PatientSkyAppInterceptor patientSkyAppInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(patientSkyAppInterceptor);
	}

}
