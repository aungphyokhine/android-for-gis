package com.tg.google.map.common;

import java.util.List;

public class ModelCacheLocation {
	private int id;
    private long date;
    private long start_date_time;
    private String type;
    private List<Double> loc;
 
    public ModelCacheLocation(){}
 
    public ModelCacheLocation(long date,List<Double> loc) {
        super();
        this.date = date;
        this.loc = loc;
    }

	public long getStart_date_time() {
		return start_date_time;
	}

	public void setStart_date_time(long start_date_time) {
		this.start_date_time = start_date_time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
