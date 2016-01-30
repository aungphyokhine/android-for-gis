package com.tg.google.map.common;

import java.util.Date;

public class ModelRequestInfo {
	private String to_uid;
    private Date created_at;
    private String from_uid;
    private Boolean approved;
	public String getTo_uid() {
		return to_uid;
	}
	public void setTo_uid(String to_uid) {
		this.to_uid = to_uid;
	}
	public Date getCreated_at() {
		return created_at;
	}
	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}
	public String getFrom_uid() {
		return from_uid;
	}
	public void setFrom_uid(String from_uid) {
		this.from_uid = from_uid;
	}
	public Boolean getApproved() {
		return approved;
	}
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}
	
     
}
