package com.eg.test.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarEntry implements Comparable<CalendarEntry>{
	
	private LocalDateTime start;
    private LocalDateTime end;

    public CalendarEntry(String start, String end) {
        this.start = LocalDateTime.parse(start, DateTimeFormatter.ISO_DATE_TIME);
        this.end = LocalDateTime.parse(end, DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

	@Override
	public int compareTo(CalendarEntry other) {
        return this.start.compareTo(other.start);
    }

}
