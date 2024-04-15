package com.eg.test.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestClass {
	
	private String calendarId;
	private long duration;
	private String periodToSearch;
	private String slotType;
	
	public String getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getPeriodToSearch() {
		return periodToSearch;
	}
	public void setPeriodToSearch(String periodToSearch) {
		this.periodToSearch = periodToSearch;
	}
	public String getSlotType() {
		return slotType;
	}
	public void setSlotType(String slotType) {
		this.slotType = slotType;
	}
	
	

}
