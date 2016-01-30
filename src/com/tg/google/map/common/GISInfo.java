package com.tg.google.map.common;

import java.util.List;

public class GISInfo {
	private long date;
	private List<Double> loc;
	private String type;
	private long start_date_time;
	private String datestring;

	public long getStart_date_time() {
		return start_date_time;
	}

	public void setStart_date_time(long start_date_time) {
		this.start_date_time = start_date_time;
	}



	public String getDatestring() {
		return datestring;
	}



	public void setDatestring(String datestring) {
		this.datestring = datestring;
	}



	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public List<Double> getLoc() {
		return loc;
	}

	public void setLoc(List<Double> loc) {
		this.loc = loc;
	}
	
	
}



