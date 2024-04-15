package com.eg.test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.eg.test.model.ResponseClass;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.common.util.StringUtils;

@Component
public class PatientSkyAppInterceptor implements HandlerInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(PatientSkyAppInterceptor.class);
	
	@Autowired
	private Environment appProps;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handle) throws Exception{
		try {
			final String appJson = "application/json";
			ResponseClass appXssResponse = xssValidation(request);
			if(Boolean.TRUE.equals(appXssResponse.getIsError())) {
				response.setContentType(appJson);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ObjectMapper objMap = new ObjectMapper();
				response.getWriter().write(objMap.writeValueAsString(appXssResponse));
				return false;
			}
			
			final String reqMethod = request.getMethod();
			String allowedMethods = appProps.getProperty("security.cors.allowedMethods");
			boolean found = false;
			for(String allowed: allowedMethods.split(",")) {
				if(reqMethod.equalsIgnoreCase(allowed)) {
					found = true;
				}
			}
			if(!found) {
				response.setContentType(appJson);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return false;
			}
		}catch(Exception ex) {
			logger.error("Error in preHandle()");
		}
		return true;
	}

	private ResponseClass xssValidation(HttpServletRequest request) {
		ResponseClass response = new ResponseClass();
		try {
			String[] headerNames = null;
			if(StringUtils.isNotBlank(appProps.getProperty("security.cors.allowedHeaders"))) {
				headerNames = appProps.getProperty("security.cors.allowedHeaders").split(",");
			}
			if(headerNames != null) {
				for(String header: headerNames) {
					if(StringUtils.isNotBlank(header) && (!xssPatternMatcher(StringUtils.isNotBlank(request.getHeader(header)) ? request.getHeader(header) : ""))) {
						response.setIsError(true);
						return response;
					}
				}
			}
		}catch(Exception ex) {
			response.setIsError(true);
			return response;
		}
		response.setIsError(false);
		response.setData(null);
		return response;
	}

	private boolean xssPatternMatcher(String value) {
		try {
			if(StringUtils.isBlank(value)) {
				return true;
			}
			value = value.toLowerCase().replaceAll("\\s", "");
			if(checkForXssValidation(value, "(.*?)<script>(.*?)</script>(.*?)")) {
				return false;
			}
			if(checkForXssValidation(value, "(.*?)<script(.*?)>(.*?)")) {
				return false;
			}
			if(checkForXssValidation(value, "(.*?)</script(.*?)>(.*?)")) {
				return false;
			}
			if(checkForXssValidation(value, "(.*?)javascript:(.*?)")) {
				return false;
			}
			if(checkForXssValidation(value, "(.*?)vbscript:(.*?)")) {
				return false;
			}
			if(checkForXssValidation(value, "(.*?)expression\\((.*?)\\)(.*?)")) {
				return false;
			}
			if(checkForXssValidation(value, "(.*?)eval\\((.*?)\\)(.*?)")) {
				return false;
			}
		} catch (Exception ex) {
			logger.error("Error in xssPatternMatcher()", ex);
			return false;
		}
		return true;
	}
	
	private boolean checkForXssValidation(String value, String inputPattern) {
		return value.matches(inputPattern);
	}

}
