package com.eg.test.service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import com.eg.test.model.CalendarEntry;
import com.eg.test.model.RequestClass;
import com.eg.test.model.ResponseClass;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.micrometer.common.util.StringUtils;

@Service
@RequestScope
public class PatientSkyAppService {
	
	@Autowired
	private Environment appProps;
	
	private JSONArray convertJsonNodeToJsonArray(JsonNode jsonNode, String calenderId) throws JsonProcessingException {
//        JSONArray jsonArray = new JSONArray();
//        for (JsonNode node : jsonNode) {
//        	if(StringUtils.isNotBlank(calenderId) && !node.get("calendar_id").toString().replace("\"", "").equalsIgnoreCase(calenderId)) {
//        		//break the current iteration if the input calendar id does not match
//        		break;
//        	}
//            jsonArray.put(node);
//        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStringFromJsonNode = objectMapper.writeValueAsString(jsonNode);
        JSONArray jsonArray = new JSONArray(jsonStringFromJsonNode);
        return jsonArray;
    }
	
	public ResponseClass findAvailableSlots(HttpHeaders inputHeaders, RequestClass request) {
		ResponseClass response = new ResponseClass();
		String calenderId = request.getCalendarId();
		long length = request.getDuration();
		String periodToSearch = request.getPeriodToSearch().replace("Z", "");
		LocalDateTime start = LocalDateTime.parse(periodToSearch.split("/")[0], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(periodToSearch.split("/")[1], DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String slotType = request.getSlotType();
		
		JSONArray appointments = new JSONArray();
		JSONArray timeSlots = new JSONArray();
		
		String folderPath = appProps.getProperty("application.path.resources");
		try {
			File folder = new File(folderPath);
	        File[] files = folder.listFiles();
	        if (files != null) {
	        	for (File file : files) {
	        		if (file.isFile()) {
	        			ObjectMapper objectMapper = new ObjectMapper();
	                    JsonNode jsonNode = objectMapper.readTree(file);
	                    if(jsonNode.get("appointments").get(0).get("calendar_id").toString().replace("\"", "").equalsIgnoreCase(calenderId)) {
	                    	appointments.putAll(convertJsonNodeToJsonArray(jsonNode.get("appointments"), calenderId));
	                    }
	                    if(jsonNode.get("timeslots").get(0).get("calendar_id").toString().replace("\"", "").equalsIgnoreCase(calenderId)) {
	                    	timeSlots.putAll(convertJsonNodeToJsonArray(jsonNode.get("timeslots"), calenderId));
	                    }
	                }
	            }
	        }
	        JSONArray result = new JSONArray();
//	        List<CalendarEntry> allEntries = new ArrayList<>();
//	        for(int j = 0; j < timeSlots.length(); j++) {
//        		JSONObject appintmentObject = timeSlots.getJSONObject(j);
//        		CalendarEntry calendar = new CalendarEntry(appintmentObject.getString("start"), appintmentObject.getString("end"));
//        		allEntries.add(calendar);
//	        }
//	        //List<CalendarEntry> merged = mergeCalendars(allEntries);
//	        List<String> availableSlots = new ArrayList<>();
//
//	        LocalDateTime currentTime = start;
//	        for (CalendarEntry busy : allEntries) {
//	            while ((busy.getStart().isAfter(currentTime) || busy.getStart().isEqual(currentTime)) && (busy.getEnd().isBefore(currentTime) || busy.getEnd().isEqual(currentTime))) {
//	                availableSlots.add(currentTime.format(DateTimeFormatter.ISO_DATE_TIME) + "/"
//	                        + currentTime.plusMinutes(length).format(DateTimeFormatter.ISO_DATE_TIME));
//	                currentTime = currentTime.plusMinutes(length);
//	            }
//	            currentTime = busy.getEnd();
//	        }

//	        while (!currentTime.isAfter(end)) {
//	            availableSlots.add(currentTime.format(DateTimeFormatter.ISO_DATE_TIME) + "/"
//	                    + currentTime.plusMinutes(length).format(DateTimeFormatter.ISO_DATE_TIME));
//	            currentTime = currentTime.plusMinutes(length);
//	        }
	        
	        for(int i = 0; i < timeSlots.length(); i++) {
	        	JSONObject jsonObject = timeSlots.getJSONObject(i);
	        	LocalDateTime startTime = LocalDateTime.parse(jsonObject.getString("start"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	            LocalDateTime endTime = LocalDateTime.parse(jsonObject.getString("end"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	            Duration duration = Duration.between(startTime, endTime);
	            long minutes = duration.toMinutes();
	            if(minutes > length && (start.isBefore(startTime) || start.isEqual(startTime)) && (end.isAfter(endTime) || end.isEqual(endTime))) {
	            	JSONObject resultObj = new JSONObject();
	            	for(int j = 0; j < appointments.length(); j++) {
	            		JSONObject appintmentObject = appointments.getJSONObject(j);
	            		LocalDateTime appointmentStartTime = LocalDateTime.parse(appintmentObject.getString("start"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	    	            LocalDateTime appointmentEndTime = LocalDateTime.parse(appintmentObject.getString("end"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	    	            if(!appointmentStartTime.isEqual(startTime) && !appointmentEndTime.isEqual(endTime)) {
	    	            	resultObj.put("start", jsonObject.getString("start"));
	    	            	resultObj.put("end", jsonObject.getString("end"));
	    	            	result.put(resultObj);
	    	            	break;
	    	            }
	            	}
	            }
	        }
	        response.setIsError(false);
	        response.setData(result.toList());
		} catch (IOException e) {
			e.printStackTrace();
	    }
		return response;
	}
	
	private static List<CalendarEntry> mergeCalendars(List<CalendarEntry> entries) {
        Collections.sort(entries);
        List<CalendarEntry> merged = new ArrayList<>();
        CalendarEntry last = null;

        for (CalendarEntry current : entries) {
            if (last == null || last.getEnd().isBefore(current.getStart())) {
                merged.add(current);
                last = current;
            } else if (last.getEnd().isBefore(current.getEnd())) {
                last = new CalendarEntry(last.getStart().toString(), current.getEnd().toString());
                merged.set(merged.size() - 1, last);
            }
        }
        return merged;
    }


}
