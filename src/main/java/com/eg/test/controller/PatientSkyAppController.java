package com.eg.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.eg.test.model.RequestClass;
import com.eg.test.model.ResponseClass;
import com.eg.test.service.PatientSkyAppService;

import jakarta.validation.Valid;

@RestController
@Validated
public class PatientSkyAppController {
	
	private static final Logger logger = LoggerFactory.getLogger(PatientSkyAppController.class);
	
	@Autowired
	private PatientSkyAppService patientSkyAppService;
	
	@Validated
	@RequestMapping(
			value = "/getCalendar",
			method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_VALUE},
			consumes = {MediaType.APPLICATION_JSON_VALUE}
	)
	public ResponseClass getCalendar(@RequestHeader final HttpHeaders inputHeaders, @Valid @RequestBody final RequestClass request) {
		ResponseClass response = new ResponseClass();
		try {
			response = patientSkyAppService.findAvailableSlots(inputHeaders, request);
		}catch (Exception ex) {
			logger.error("Error in getCalendar()", ex);
			response = new ResponseClass();
			response.setIsError(true);
		}
		return response;
	}

}
