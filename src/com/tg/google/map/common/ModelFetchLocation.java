package com.tg.google.map.common;

public class ModelFetchLocation extends ModelCacheLocation{
	private String uid;
	private String datestring;

	public ModelFetchLocation() {
		super();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getDatestring() {
		return datestring;
	}

	public void setDatestring(String datestring) {
		this.datestring = datestring;
	}
	
}
