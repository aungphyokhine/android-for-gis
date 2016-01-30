package com.tg.google.map.common;

import java.util.List;


public class GISInfoData{
	public List<GISInfo> items;
	public Boolean success;
	private String email;

	
	public GISInfoData(List<GISInfo> items, Boolean success, String email) {
		super();
		this.items = items;
		this.success = success;
		this.email = email;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public List<GISInfo> getItems() {
		return items;
	}
	public void setItems(List<GISInfo> items) {
		this.items = items;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
}