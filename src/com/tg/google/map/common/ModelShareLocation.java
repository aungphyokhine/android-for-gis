package com.tg.google.map.common;

import java.util.List;

public class ModelShareLocation {
	private long date;
	private String message;
	private List<Double> loc;
	
	
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Double> getLoc() {
		return loc;
	}
	public void setLoc(List<Double> loc) {
		this.loc = loc;
	}
	
}
